package com.template.contract

import com.template.command.Commands
import com.template.state.PartnerState
import com.template.state.TokenState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class PartnerContract : Contract {
    companion object {
        val PARTNER_CONTRACT_ID = "com.template.contract.PartnerContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.get(0)
        when (command.value) {
            is Commands.PartnerAdd -> {
                val stateOutput = tx.outputs.get(0).data as PartnerState
                requireThat {
                    "Транзакция не должна иметь входые данные" using (tx.inputs.isEmpty())
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "Должен быть указать номер кошелька" using (stateOutput.walletId != null)
                    "Банк должен являться участником данной транзакции" using (stateOutput.currentParty.name.organisation == "Bank")
                    "Клиент не может являться участником данной транзакции" using (stateOutput.patrnerParty.name.organisation != "Client")
                    //Могут быть еще условия
                }
            }
            is Commands.PartnerUpdate -> {
                val stateOutput = tx.outputs.get(0).data as PartnerState
                requireThat {
                    "Транзакция должна иметь входые данные" using (tx.inputs.isEmpty())
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "Клиент не может являться участником данной транзакции" using (stateOutput.currentParty.name.organisation != "Client")
                    "Банк должен являться участником данной транзакции" using (stateOutput.patrnerParty.name.organisation == "Bank")
                    //Могут быть еще условия
                }
            }
            else -> {
                //Todo Вывести, что команда не распознана
            }
        }
    }
}