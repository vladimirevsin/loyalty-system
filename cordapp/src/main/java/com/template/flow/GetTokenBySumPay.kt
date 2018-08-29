package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.schema.ClientSchemaV1
import com.template.schema.PartnerSchemaV1
import com.template.state.ClientState
import com.template.state.PartnerState
import com.template.state.TokenState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.util.*

object GetTokenBySumPay {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val sumPay: Double) : FlowLogic<Double?>() {
        companion object {
            object GET_DATA : ProgressTracker.Step("-------.")

            fun tracker() = ProgressTracker(
                    GET_DATA
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): Double? {
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)
            //
            progressTracker.currentStep = GET_DATA
            val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)
            val results = serviceHub.vaultService.queryBy<PartnerState>(criteria)
            val partnerData = if (results.states.size > 0) results.states.last() else null
            val stateData = if (partnerData != null) partnerData.state.data else throw Exception("Параметры ТСП не настроены")
            return if (stateData.minAmount < sumPay) stateData.exchangeRate * sumPay else 0.0
        }
    }
}