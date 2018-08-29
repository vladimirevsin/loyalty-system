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

data class PartnerState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                        val currentParty: Party,
                        val patrnerParty: Party,
                        val partnerId: UUID,
                        val partnerName: String,
                        val partnerInn: String,
                        val partnerAddInfo: String,
                        val allianceId: Int,
                        val walletId: UUID,
                        val exchangeRate: Double,
                        val percentageAmount: Double,
                        val minAmount: Double) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(currentParty, patrnerParty)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is PartnerSchemaV1 -> PartnerSchemaV1.PersistentPartner(
                linearID = linearId.id,
                partnerId = partnerId,
                partnerName = partnerName,
                partnerInn = partnerInn,
                partnerAddInfo = partnerAddInfo,
                allianceId = allianceId,
                walletId = walletId,
                exchangeRate = exchangeRate,
                percentageAmount = percentageAmount,
                minAmount = minAmount
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(PartnerSchemaV1)
}