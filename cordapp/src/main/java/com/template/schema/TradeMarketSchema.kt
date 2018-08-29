package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object TradeMarketSchema

object TradeMarketSchemaV1 : MappedSchema(
        schemaFamily = TradeMarketSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTradeMarket::class.java)) {

    @Entity(name="TradeMarket")
    class PersistentTradeMarket(
        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "tradeId")
        private val tradeId: UUID,

        @Column(name = "saleWalletCustomer")
        private val saleWalletCustomer: UUID,

        @Column(name = "buyWalletMarket")
        private val buyWalletMarket: UUID,

        @Column(name = "saleWalletMarket")
        private val saleWalletMarket: UUID,

        @Column(name = "buyWalletCustomer")
        private val buyWalletCustomer: UUID,

        @Column(name = "tradeDate")
        private val tradeDate: Date,

        @Column(name = "commissionRate")
        private val commissionRate: Double,

        @Column(name = "saleCountToken")
        private val saleCountToken: Double,

        @Column(name = "buyCountToken")
        private val buyCountToken: Double
        ) : PersistentState() {
        constructor(): this(UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Date(),
                0.0, 0.0, 0.0)
    }
}