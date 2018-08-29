package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.schema.AggregateBalanceSchemaV1
import com.template.schema.ClientSchemaV1
import com.template.schema.WalletSchemaV1
import com.template.state.*
import net.corda.core.flows.*
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.utilities.ProgressTracker

object GetBalanceClient {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val phoneNumber: String) : FlowLogic<TransactionResultData>() {
        companion object {
            object GET_DATA : ProgressTracker.Step("-------.")

            fun tracker() = ProgressTracker(
                    GET_DATA
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): TransactionResultData {
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)
            //
            progressTracker.currentStep = GET_DATA
            val ownerId = builder {
                val ownerPredicate = ClientSchemaV1.PersistentClient::phoneNumber.equal(phoneNumber)
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
                val criteria = generalCriteria.and(walletCriteria)
                serviceHub.vaultService.queryBy<WalletState>(criteria)
            }
            var walletState : MutableList<WalletState>? = mutableListOf<WalletState>()
            if (walletId.states.size > 0) walletId.states.forEach { it -> walletState!!.add(it.state.data) }
            else walletState = null

            if (walletState == null) throw Exception("Кошелек не определен")
            //Определение баланса кошелька на момент трнзакции

            var wallets: MutableList<Wallet> = mutableListOf()
            for (wallet in walletState) {
                val resultsBalanceState = builder {
                    val walletPredicate = AggregateBalanceSchemaV1.PersistentAggregateBalance::client.equal(wallet.walletId)
                    val walletCriteria = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)

                    val walletPredicate2 = AggregateBalanceSchemaV1.PersistentAggregateBalance::markerAlliance.equal(wallet.allianceTokenId)
                    val walletCriteria2 = QueryCriteria.VaultCustomQueryCriteria(walletPredicate)

                    val criteria = generalCriteria.and(walletCriteria)
                    serviceHub.vaultService.queryBy<AggregateBalanceState>(criteria)
                }

                val balance = if (resultsBalanceState.states.size > 0) resultsBalanceState.states.last().state.data.newValue else 0.0
                wallets.add(Wallet(wallet.walletId, balance, wallet.allianceTokenId))


            }

            return TransactionResultData(wallets.toList(), 0, "Ok", "")//balance
        }
    }

}