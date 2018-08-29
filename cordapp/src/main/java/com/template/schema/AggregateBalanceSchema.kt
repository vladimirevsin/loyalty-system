package com.template.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object AggregateBalanceSchema

object AggregateBalanceSchemaV1 : MappedSchema(
        schemaFamily = AggregateBalanceSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentAggregateBalance::class.java)) {

    @Entity(name="Aggregate_Balance")
    class PersistentAggregateBalance(

            @Column(name = "linear_id")
            public val linearID: UUID,

            @Column(name = "clientId")
            public val client: UUID?,

            @Column(name = "balanceValue")
            public val balanceValue: Double,

            @Column(name = "markerAlliance")
            public val markerAlliance: Int,

            @Column(name = "modifiedDate")
            public val modifiedDate: String

            ) : PersistentState() {
        constructor(): this(UUID.randomUUID(),
                null, 0.0, 0, "")
    }
}