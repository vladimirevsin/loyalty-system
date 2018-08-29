package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object PartnerSchema

object PartnerSchemaV1 : MappedSchema(
        schemaFamily = PartnerSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentPartner::class.java)) {

    @Entity(name="Partner")
    class PersistentPartner(
        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "partnerId")
        private val partnerId: UUID,

        @Column(name = "partnerName")
        private val partnerName: String,

        @Column(name = "partnerInn")
        private val partnerInn: String,

        @Column(name = "partnerAddInfo")
        private val partnerAddInfo: String,

        @Column(name = "allianceId")
        private val allianceId: Int,

        @Column(name = "walletId")
        private val walletId: UUID,

        /**
         * Курс обмена - стоймость 1 балла
         */
        @Column(name = "exchangeRate")
        private val exchangeRate: Double,

        /**
         * Процент от суммы покупки
         */
        @Column(name = "percentageAmount")
        private val percentageAmount: Double,

        /**
         * Минимальная сумма покупки для начисления баллов
         */
        @Column(name = "minAmount")
        private val minAmount: Double
    ) : PersistentState() {
        constructor(): this(UUID.randomUUID(),
                UUID.randomUUID(),"", "", "", 0, UUID.randomUUID(),
                0.0, 0.0, 0.0)
    }
}