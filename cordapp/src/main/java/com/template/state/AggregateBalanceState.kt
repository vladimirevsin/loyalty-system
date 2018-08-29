package com.template.state

import com.template.schema.AggregateBalanceSchemaV1
import com.template.schema.TokenPaperSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class AggregateBalanceState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                                 val clientId: UUID,
                                 val newValue: Double,
                                 val marker: Int,
                                 val parties: List<Party>) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = parties

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is AggregateBalanceSchemaV1 -> AggregateBalanceSchemaV1.PersistentAggregateBalance(
                    linearID = linearId.id,
                    balanceValue = newValue,
                    client = clientId,
                    markerAlliance = marker,
                    modifiedDate = Date().toString()
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(AggregateBalanceSchemaV1)
}