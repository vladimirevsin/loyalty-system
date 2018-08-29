package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object AllianceSchema

object AllianceSchemaV1 : MappedSchema(
        schemaFamily = AllianceSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentAlliance::class.java)) {

    @Entity(name="Alliance")
    class PersistentAlliance(
        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "allianceId")
        private val allianceId: Int,

        @Column(name = "allianceName")
        private val allianceName: String,

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
                0,"",0.0, 0.0, 0.0)
    }
}