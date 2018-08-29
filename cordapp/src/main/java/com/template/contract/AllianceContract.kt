package com.template.contract

import com.template.command.Commands
import com.template.state.AllianceState
import com.template.state.PartnerState
import com.template.state.TokenState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class AllianceContract : Contract {
    companion object {
        val ALLIANCE_CONTRACT_ID = "com.template.contract.AllianceContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.get(0)
        when (command.value) {
            is Commands.AllianceAdd -> {
                val stateOutput = tx.outputs.get(0).data as AllianceState
                requireThat {
                    "Транзакция не должна иметь входные данные" using (tx.inputs.isEmpty())
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    //Могут быть еще условия
                }
            }
            else -> {
                //Todo Вывести, что команда не распознана
            }
        }
    }
}