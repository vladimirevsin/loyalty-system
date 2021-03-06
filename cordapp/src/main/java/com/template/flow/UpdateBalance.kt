package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.template.command.Commands
import com.template.contract.TokenContract
import com.template.state.AggregateBalanceState
import com.template.state.TokenState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

object UpdateBalance {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val walletID: UUID,
                    val value: Double,
                    val marker: Int) : FlowLogic<SignedTransaction?>() {
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
        override fun call(): SignedTransaction? {
            try {
                val currentParty = serviceHub.myInfo.legalIdentities.get(0)
                val notary = serviceHub.networkMapCache.notaryIdentities.get(0)
                var partiesMutable: MutableList<Party> = mutableListOf<Party>()
                val allNodes = serviceHub.networkMapCache.allNodes.toList()
                        .filter { it -> it.legalIdentities[0] != currentParty && it.legalIdentities[0] != notary }
                        .forEach { it -> partiesMutable.add(it.legalIdentities[0] as Party) }

                val parties = partiesMutable.toList()
                partiesMutable.add(currentParty)
                val allParty: List<Party> = partiesMutable.toList()
                progressTracker.currentStep = GENERATING_TRANSACTION;
                val tokenState = AggregateBalanceState(
                        linearId = UniqueIdentifier(),
                        marker = marker,
                        clientId = walletID,
                        newValue = value,
                        parties = allParty
                )

                val txCommand = Command(Commands.UpdateBalance(),
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
                var flowSessions: MutableList<FlowSession> = mutableListOf()
                parties.forEach { it -> flowSessions.add(initiateFlow(it)) }
                val sigs = flowSessions.toSet()
                progressTracker.currentStep = GATHERING_SIGS
                val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, sigs))

                progressTracker.currentStep = FINALISING_TRANSACTION
                val subFlow = subFlow(FinalityFlow(fullySignedTx))
                return subFlow
            } catch (e: Exception) {
                return null
            }
        }
    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data as AggregateBalanceState
                    "Значение средств меньше нуля" using (output.newValue >= 0)
                }
            }
            return subFlow(signTransactionFlow)
        }
    }
}