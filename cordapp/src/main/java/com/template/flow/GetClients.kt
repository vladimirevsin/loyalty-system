package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.dto.Client
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
import net.corda.core.node.services.vault.DEFAULT_PAGE_NUM
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.util.*

object GetClients {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val clientParty: Party) : FlowLogic<List<Client>>() {
        companion object {
            object GET_DATA : ProgressTracker.Step("-------.")

            fun tracker() = ProgressTracker(
                    GET_DATA
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): List<Client> {
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)
            //
            progressTracker.currentStep = GET_DATA
            val otherPartyFlow = initiateFlow(clientParty)
            val clientList = otherPartyFlow.receive<List<Client>>().unwrap { data -> data }
            return clientList
        }
    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<Void?>() {

        @Suspendable
        override fun call(): Void? {

            val criteria = QueryCriteria.VaultQueryCriteria(status = Vault.StateStatus.ALL)

            val results = serviceHub.vaultService.queryBy<ClientState>(criteria)
            val clientList = if (results.states.size > 0) results.states else null
            var listStates = mutableListOf<Client>()
            if (results.states.size > 0)
                results.states!!.forEach { it -> listStates.add(Client(it.state.data.FIO, it.state.data.phoneNumber, it.state.data.linearId.id)) }

            otherPartyFlow.send(listStates.toList())
            return null
        }

    }
}