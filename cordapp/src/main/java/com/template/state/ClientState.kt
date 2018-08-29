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

data class ClientState(override val linearId: UniqueIdentifier = UniqueIdentifier(),
                       val currentParty: Party,
                       val phoneNumber: String,
                       val FIO: String,
                       val password: String) : LinearState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(currentParty)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is ClientSchemaV1 -> ClientSchemaV1.PersistentClient(
                linearID = linearId.id,
                phoneNumber = phoneNumber,
                FIO = FIO,
                password = password
            )
            else -> throw IllegalArgumentException("Неподдерживаемая схема $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(ClientSchemaV1)
}