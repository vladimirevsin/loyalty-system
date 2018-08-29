package com.template.state

import com.template.schema.*
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class AllianceState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                         val parties: List<Party>,
                         val allianceId: Int,
                         val allianceName: String,
                         val exchangeRate: Double,
                         val percentageAmount: Double,
                         val minAmount: Double) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = parties

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is AllianceSchemaV1 -> AllianceSchemaV1.PersistentAlliance(
                linearID = linearId.id,
                allianceId = allianceId,
                allianceName = allianceName,
                exchangeRate = exchangeRate,
                percentageAmount = percentageAmount,
                minAmount = minAmount
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(AllianceSchemaV1)
}