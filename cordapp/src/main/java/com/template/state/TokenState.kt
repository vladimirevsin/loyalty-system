package com.template.state

import com.template.schema.TokenPaperSchema
import com.template.schema.TokenPaperSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*

data class TokenState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                      val sender: Party,
                      val recipient: Party,
                      val value: Double,
                      val marker: Int,
                      val clientSender: UUID?,
                      val clientRecipient: UUID?,
                      val balanceSender: Double) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(sender, recipient)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is TokenPaperSchemaV1 -> TokenPaperSchemaV1.PersistentTokenPaper(
                    linearID = linearId.id,
                    sender = sender.name.toString(),
                    pecipient = recipient.name.toString(),
                    marker = marker,
                    value = value,
                    clientReciepent = clientRecipient,
                    clientSender =  clientSender
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(TokenPaperSchemaV1)
}