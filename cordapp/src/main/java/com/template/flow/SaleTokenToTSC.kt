package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.schema.AggregateBalanceSchemaV1
import com.template.state.AggregateBalanceState
import com.template.state.PartnerState
import com.template.state.TokenState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.VaultService
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import net.corda.finance.contracts.asset.Cash
import org.hibernate.loader.custom.CustomQuery
import java.util.*

object SaleTokenToTSC {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val tscParty: Party,
                    val value: Double,
                    val marker: Int) : FlowLogic<TransactionResultData>() {
        companion object {
            object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new IOU.")
            object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
            object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object UPDATE_WALLET_TSC : ProgressTracker.Step("Updating wallet client")
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
            try {
                val notary = serviceHub.networkMapCache.notaryIdentities[0]

                val bankParty = serviceHub.myInfo.legalIdentities.get(0)

                //
                progressTracker.currentStep = GENERATING_TRANSACTION;
                val tokenState = TokenState(
                        UniqueIdentifier(),
                        bankParty,
                        tscParty,
                        value,
                        marker,
                        null, null, 0.0
                )

                val otherPartyFlow = initiateFlow(tscParty)
                val tscWallet = otherPartyFlow.receive<Wallet>().unwrap { data -> data }

                val txCommand = Command(Commands.SellToTSC(),
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
                val finalTransaction = subFlow(FinalityFlow(fullySignedTx))

                val wallet = Wallet(tscWallet.walletId, tscWallet.value + value, tscWallet.marker)

                return TransactionResultData(listOf(wallet), 0, "ok", finalTransaction.toString())
            } catch (e: Exception) {
                return TransactionResultData(null, -1, e.message!!, "")
            }
        }

    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val pagingSpec = PageSpecification(DEFAULT_PAGE_NUM, 10)
            val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)

            //Получение данных по кошельку ТСП
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
            otherPartyFlow.send(wallet)
            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {

                override fun checkTransaction(stx: SignedTransaction) {
                    return requireThat {
                        val output = stx.tx.outputs.single().data as TokenState
                        "Значение перечисляемых средств меньше нуля" using (output.value > 0)
                    }
                }
            }
            return subFlow(signTransactionFlow)
        }
    }
}