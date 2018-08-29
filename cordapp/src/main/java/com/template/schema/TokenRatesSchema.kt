package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object TokenRatesSchema

object TokenRatesSchemaV1 : MappedSchema(
        schemaFamily = TokenRatesSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTokenRates::class.java)) {

    @Entity(name="TokenRates")
    class PersistentTokenRates(
        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "rateId")
        private val rateId: UUID,

        @Column(name = "saleAllianceId")
        private val saleAllianceId: UUID,

        @Column(name = "buyAllianceId")
        private val buyAllianceId: UUID,

        @Column(name = "exchangeRate")
        private val exchangeRate: Double,

        @Column(name = "exchangeRateDate")
        private val exchangeRateDate: Date
        ) : PersistentState() {
        constructor(): this(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), 0.0, Date())
    }
}