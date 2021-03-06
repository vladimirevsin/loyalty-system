package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import com.sun.javaws.progress.Progress
import com.template.command.Commands
import com.template.contract.ClientContract
import com.template.contract.TokenContract
import com.template.dto.TransactionResultData
import com.template.dto.Wallet
import com.template.state.*
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

object AddWallet {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(val clientId: UUID,
                    val marker: Int) : FlowLogic<TransactionResultData>() {
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
            val clientParty = serviceHub.myInfo.legalIdentities.get(0)
            val walletId = UUID.randomUUID()
            //
            progressTracker.currentStep = GENERATING_TRANSACTION;
            val walletState = WalletState(
                    linearId = UniqueIdentifier(),
                    currentParty = clientParty,
                    allianceTokenId = marker,
                    balance = 0.0,
                    ownerId = clientId,
                    walletId = walletId
            )

            val txCommand = Command(Commands.WalletAdd(),
                    walletState.participants.map { it.owningKey })
            val txBuilder = TransactionBuilder(notary)
                    .addOutputState(walletState, ClientContract.CLIENT_CONTRACT_ID)
                    .addCommand(txCommand)

            //
            progressTracker.currentStep = VERIFYING_TRANSACTION
            txBuilder.verify(serviceHub)

            //
            progressTracker.currentStep = SIGNING_TRANSACTION
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

            progressTracker.currentStep = FINALISING_TRANSACTION;
            val subFlow = subFlow(FinalityFlow(partSignedTx))
            return TransactionResultData(
                    listOf(Wallet(walletId, 0.0, marker)),
                    0, "Ok", subFlow.toString()
            )
        }
    }
}