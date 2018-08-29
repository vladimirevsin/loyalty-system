package com.template.contract

import com.template.command.Commands
import com.template.flow.AddTokenToClient
import com.template.state.AggregateBalanceState
import com.template.state.TokenState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class TokenContract : Contract {
    companion object {
        val TOKEN_CONTRACT_ID = "com.template.contract.TokenContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.get(0)
        when (command.value) {
            is Commands.SellToTSC -> {
                val stateOutput = tx.outputs.get(0).data as TokenState
                requireThat {
                    "Транзакция не должна иметь входов" using (tx.inputs.isEmpty())
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "Банк должен являться участником данной транзакции" using (stateOutput.sender.name.organisation == "Bank")
                    "Клиент не может являться участником данной транзакции" using (stateOutput.sender.name.organisation != "Clients")
                    "Количество токенов должно быть больше нуля" using (stateOutput.value > 0)
                }
            }
            is Commands.AddBonusClient -> {
                val stateOutput = tx.outputs.get(0).data as TokenState
                requireThat {
                    "Клиент должен являться участником данной транзакции" using (stateOutput.clientRecipient != null)
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "У ТСП должно хватать средств" using (stateOutput.value >= 0)
                }
            }
            is Commands.DestroyBonusClient -> {
                val stateOutput = tx.outputs.get(0).data as TokenState
                requireThat {
                    "Клиент должен являться участником данной транзакции" using (stateOutput.clientSender != null)
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "У клиента должно хватать средств" using (stateOutput.value >= 0)
                }
            }
            is Commands.UpdateBalance -> {
                val stateOutput = tx.outputs.single().data as AggregateBalanceState
                requireThat {
                    "Транзакция должна содержать идентификатор кошелька участника сети" using (stateOutput.clientId != null)
                    "Транзакция должна иметь хотя бы одно состояние выхода" using (tx.outputs.size > 0)
                    "Количество токенов должно быть больше нуля" using (stateOutput.newValue >= 0)
                }
            }
            else -> {
                //Todo Вывести, что команда не распознана
            }
        }
    }
}