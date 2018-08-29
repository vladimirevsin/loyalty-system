package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.schema.AggregateBalanceSchemaV1
import com.template.schema.ClientSchemaV1
import com.template.schema.WalletSchemaV1
import com.template.state.*
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.util.*

object AddTokenToClient {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val clientParty: Party,
                    val value: Double,
                    val marker: Int,
                    val phoneNumber: String) : FlowLogic<TransactionResultData>() {
        companion object {
            object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new IOU.")
            object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
            object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): TransactionResultData {
            val notary = serviceHub.networkMapCache.notaryIdentities[0]
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)

            // Получаем баланс и идентификатор кошелька TSC
            val pagingSpec = PageSpecification(DEFAULT_PAGE_NUM, 10)
            val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)

            val results = serviceHub.vaultService.queryBy<PartnerState>(criteria, paging = pagingSpec)
            val partnerData = if (results.states.size > 0) results.states.last() else null
            val stateData = partnerData!!.state.data

            //Определение баланса кошелька на момент трнзакции
            val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)

            val resultsBalanceState = builder {
                val walletPredicate = AggregateBalanceSchemaV1.PersistentAggregateBalance::client.equal(stateData.walletId)
                val walletCriteria = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)
                val criteria = generalCriteria.and(walletCriteria)
                serviceHub.vaultService.queryBy<AggregateBalanceState>(criteria)
            }

            val balance = if (resultsBalanceState.states.size > 0) resultsBalanceState.states.last().state.data.newValue
            else 0.0
            val wallet = Wallet(stateData.walletId, balance, stateData.allianceId)
            val otherPartyFlow = initiateFlow(clientParty)

            val clientData = otherPartyFlow.sendAndReceive<List<Any>>(listOf(phoneNumber, marker)).unwrap { data -> data }

            progressTracker.currentStep = GENERATING_TRANSACTION;
            val tokenState = TokenState(
                    linearId = UniqueIdentifier(),
                    sender = tscParty,
                    recipient = clientParty,
                    value = value,
                    marker = marker,
                    clientSender = null,
                    clientRecipient = clientData[0] as UUID?,
                    balanceSender = wallet.value - value
            )


            val txCommand = Command(Commands.AddBonusClient(),
                    tokenState.participants.map { it.owningKey })
            val txBuilder = TransactionBuilder(notary)
                    .addOutputState(tokenState, TokenContract.TOKEN_CONTRACT_ID)
                    .addCommand(txCommand)

            //
            progressTracker.currentStep = VERIFYING_TRANSACTION
            txBuilder.verify(serviceHub)

            //
            progressTracker.currentStep = SIGNING_TRANSACTION
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

            //
            progressTracker.currentStep = GATHERING_SIGS
            val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(otherPartyFlow)));


            progressTracker.currentStep = FINALISING_TRANSACTION;
            val subFlowFinally = subFlow(FinalityFlow(fullySignedTx))

            val walletTSC = Wallet(stateData.walletId, balance - value, stateData.allianceId)
            val walletClient = clientData[1] as Wallet
            val walletCl = Wallet(walletClient.walletId, walletClient.value + value, walletClient.marker)
            return TransactionResultData(
                    listOf(walletCl, walletTSC),
                    0, "Ok", subFlowFinally.toString()
            )
        }

    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            // Получаем баланс и идентификатор кошелька клиента
            val clientIdent = otherPartyFlow.receive<List<Any>>().unwrap{ data -> data }

            val ownerId = builder {
                val ownerPredicate = ClientSchemaV1.PersistentClient::phoneNumber.equal(clientIdent[0] as String)
                val ownerCriteria = QueryCriteria.VaultCustomQueryCriteria(ownerPredicate)
                val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)

                val criteria = generalCriteria.and(ownerCriteria)
                serviceHub.vaultService.queryBy<ClientState>(criteria)
            }

            val identClient = if (ownerId.states.size > 0) ownerId.states.last().state.data.linearId.id else null


            val pagingSpec = PageSpecification(DEFAULT_PAGE_NUM, 10)
            val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)


            val walletId = builder {
                val walletPredicate = WalletSchemaV1.PersistentWallet::ownerId.equal(identClient)
                val walletCriteria = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)

                val walletPredicate2 = WalletSchemaV1.PersistentWallet::allianceTokenId.equal(clientIdent[1] as Int)
                val walletCriteria2 = QueryCriteria.VaultCustomQueryCriteria(walletPredicate2)


                val criteria = generalCriteria.and(walletCriteria.and(walletCriteria2))
                serviceHub.vaultService.queryBy<WalletState>(criteria)
            }

            val walletState = if (walletId.states.size > 0) walletId.states.last().state.data else null

            if (walletState == null) throw Exception("Кошелек не определен")
            //Определение баланса кошелька на момент трнзакции

            val resultsBalanceState = builder {
                val walletPredicate = AggregateBalanceSchemaV1.PersistentAggregateBalance::client.equal(walletState.walletId)
                val walletCriteria = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)

                val walletPredicate2 = AggregateBalanceSchemaV1.PersistentAggregateBalance::markerAlliance.equal(walletState.allianceTokenId)
                val walletCriteria2 = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)

                val criteria = generalCriteria.and(walletCriteria)
                serviceHub.vaultService.queryBy<AggregateBalanceState>(criteria)
            }

            val balance = if (resultsBalanceState.states.size > 0) resultsBalanceState.states.last().state.data.newValue
            else 0.0

            otherPartyFlow.send(listOf(identClient, Wallet(walletState.walletId, balance, walletState.allianceTokenId)))

            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data as TokenState
                    "Значение перечисляемых средств меньше нуля" using (output.value > 0)
                }
            }
            return subFlow(signTransactionFlow)
        }

    }
}