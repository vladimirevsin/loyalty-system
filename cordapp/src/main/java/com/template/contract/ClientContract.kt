package com.template.contract

import com.template.command.Commands
import com.template.flow.AddTokenToClient
import com.template.state.*
import net.corda.core.contracts.Contract
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class ClientContract : Contract {
    companion object {
        val CLIENT_CONTRACT_ID = "com.template.contract.ClientContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.get(0)
        when (command.value) {
            is Commands.ClientAdd -> {
                val stateOutput = tx.outputs.get(0).data as ClientState
                requireThat {
                    "Не должно быть входных данных" using (tx.inputs.isEmpty())
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "Клиент должен являться участником данной транзакции" using (stateOutput.currentParty.name.organisation == "Clients")
                    //Могут быть еще условия
                }
            }
            is Commands.WalletAdd -> {
                val stateOutput = tx.outputs.get(0).data as WalletState
                "Клиент должен являться участником данной транзакции" using (stateOutput.currentParty.name.organisation == "Clients")
            }
            else -> {
                //Todo Вывести, что команда не распознана
            }
        }
    }
}