package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.dto.Client
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.schema.AggregateBalanceSchemaV1
import com.template.schema.ClientSchemaV1
import com.template.schema.PartnerSchemaV1
import com.template.schema.WalletSchemaV1
import com.template.state.*
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.util.*

object GetBalanceTSC {

    @InitiatingFlow
    @StartableByRPC
    class Initiator() : FlowLogic<Wallet>() {
        companion object {
            object GET_DATA : ProgressTracker.Step("-------.")

            fun tracker() = ProgressTracker(
                    GET_DATA
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): Wallet {
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)
            //
            //Получение данных по кошельку ТСП
            val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)
            val results = serviceHub.vaultService.queryBy<PartnerState>(criteria = criteria)
            val partnerData = if (results.states.size > 0) results.states.last() else null
            val stateData = partnerData!!.state.data

            progressTracker.currentStep = GET_DATA
            val balanceValue = builder {
                val ownerPredicate = AggregateBalanceSchemaV1.PersistentAggregateBalance::client.equal(stateData.walletId)
                val ownerCriteria = QueryCriteria.VaultCustomQueryCriteria(ownerPredicate)
                val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)

                val criteria = generalCriteria.and(ownerCriteria)
                serviceHub.vaultService.queryBy<AggregateBalanceState>(criteria)
            }

            val balance = if (balanceValue.states.size > 0) balanceValue.states.last().state.data.newValue
            else 0.0

            return Wallet(stateData.walletId, balance, stateData.allianceId)
        }
    }

}