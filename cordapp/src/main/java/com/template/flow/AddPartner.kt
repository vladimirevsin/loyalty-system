package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.PartnerContract
import com.template.contract.TokenContract
import com.template.state.PartnerState
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

object AddPartner {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val partnerId: UUID,
                    val partnerParty: Party,
                    val partnerName: String,
                    val partnerInn: String,
                    val partnerAddInfo: String,
                    val allianceId: Int,
                    val walletId: UUID,
                    val exchangeRate: Double,
                    val percentageAmount: Double,
                    val minAmount: Double) : FlowLogic<SignedTransaction>() {
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
        override fun call(): SignedTransaction {
            val notary = serviceHub.networkMapCache.notaryIdentities[0]
            val bankParty = serviceHub.myInfo.legalIdentities.get(0)

            //
            progressTracker.currentStep = GENERATING_TRANSACTION;
            val partnerState = PartnerState(
                UniqueIdentifier(),
                bankParty,
                partnerParty,
                partnerId,
                partnerName,
                partnerInn,
                partnerAddInfo,
                allianceId,
                walletId,
                exchangeRate,
                percentageAmount,
                minAmount
            )

            val txCommand = Command(Commands.PartnerAdd(),
                    partnerState.participants.map { it.owningKey })
            val txBuilder = TransactionBuilder(notary)
                    .addOutputState(partnerState, PartnerContract.PARTNER_CONTRACT_ID)
                    .addCommand(txCommand)

            //
            progressTracker.currentStep = VERIFYING_TRANSACTION
            txBuilder.verify(serviceHub)

            //
            progressTracker.currentStep = SIGNING_TRANSACTION
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
            val otherPartyFlow = initiateFlow(partnerParty)

            progressTracker.currentStep = GATHERING_SIGS
            val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(otherPartyFlow)));


            progressTracker.currentStep = FINALISING_TRANSACTION;
            return subFlow(FinalityFlow(fullySignedTx))
        }

    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data as PartnerState
                    "Основные данные должны быть заполнены" using (output.partnerName.isNotEmpty() && output.partnerInn.isNotEmpty())
                    "Курс балл/рубль должен быть задан" using (output.exchangeRate != 0.0)
                    //"Должно быть установлено хотя бы одно условие начислений баллов" using (output.percentageAmount != 0.0 || output.minAmount != 0.0)
                }
            }
            return subFlow(signTransactionFlow)
        }

    }
}