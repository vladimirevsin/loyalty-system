package com.template.state

import com.template.schema.TokenPaperSchema
import com.template.schema.TokenPaperSchemaV1
import com.template.schema.TradeMarketSchemaV1
import com.template.schema.WalletSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class TradeMarketState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                            val currentParty: Party,
                            val marketParty: Party,
                            val tradeId: UUID,
                            val saleWalletCustomer: UUID,
                            val buyWalletMarket: UUID,
                            val saleWalletMarket: UUID,
                            val buyWalletCustomer: UUID,
                            val tradeDate: Date,
                            val commissionRate: Double,
                            val saleCountToken: Double,
                            val buyCountToken: Double) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(currentParty, marketParty)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is TradeMarketSchemaV1 -> TradeMarketSchemaV1.PersistentTradeMarket(
                    linearID = linearId.id,
                    tradeId = tradeId,
                    saleWalletCustomer = saleWalletCustomer,
                    buyWalletMarket = buyWalletMarket,
                    saleWalletMarket = saleWalletMarket,
                    buyWalletCustomer = buyWalletCustomer,
                    tradeDate = tradeDate,
                    commissionRate = commissionRate,
                    saleCountToken = saleCountToken,
                    buyCountToken = buyCountToken
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(TradeMarketSchemaV1)
}