package com.template.state

import com.template.schema.TokenPaperSchema
import com.template.schema.TokenPaperSchemaV1
import com.template.schema.WalletSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class WalletState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                       val currentParty: Party,
                       val walletId: UUID,
                       val ownerId: UUID,
                       val allianceTokenId: Int,
                       val balance: Double) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(currentParty)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is WalletSchemaV1 -> WalletSchemaV1.PersistentWallet(
                    linearID = linearId.id,
                    walletId = walletId,
                    ownerId = ownerId,
                    allianceTokenId = allianceTokenId
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(WalletSchemaV1)
}