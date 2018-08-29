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

object SearchCLient {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val phoneNumber: String, val password: String) : FlowLogic<Boolean>() {
        companion object {
            object GET_DATA : ProgressTracker.Step("-------.")

            fun tracker() = ProgressTracker(
                    GET_DATA
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): Boolean {
            val tscParty = serviceHub.myInfo.legalIdentities.get(0)
            //
            progressTracker.currentStep = GET_DATA
            val ownerId = builder {
                val ownerPredicate = ClientSchemaV1.PersistentClient::phoneNumber.equal(phoneNumber)
                val ownerPredicate2 = ClientSchemaV1.PersistentClient::password.equal(password)
                val ownerCriteria = QueryCriteria.VaultCustomQueryCriteria(ownerPredicate)
                val ownerCriteria2 = QueryCriteria.VaultCustomQueryCriteria(ownerPredicate2)
                val generalCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL)

                val criteria = generalCriteria.and(ownerCriteria.and(ownerCriteria2))
                serviceHub.vaultService.queryBy<ClientState>(criteria)
            }

            return if (ownerId.states.size > 0) true else false

        }
    }

}