package com.template.state

import com.template.schema.TokenPaperSchema
import com.template.schema.TokenPaperSchemaV1
import com.template.schema.TokenRatesSchemaV1
import com.template.schema.WalletSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class TokenRatesState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                           val currentParty: Party,
                           val rateId: UUID,
                           val saleAllianceId: UUID,
                           val buyAllianceId: UUID,
                           val exchangeRate: Double,
                           val exchangeRateDate: Date) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(currentParty)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is TokenRatesSchemaV1 -> TokenRatesSchemaV1.PersistentTokenRates(
                    linearID = linearId.id,
                    rateId = rateId,
                    saleAllianceId = saleAllianceId,
                    buyAllianceId = buyAllianceId,
                    exchangeRate = exchangeRate,
                    exchangeRateDate = exchangeRateDate
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(TokenRatesSchemaV1)
}