package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object ClientSchema

object ClientSchemaV1 : MappedSchema(
        schemaFamily = ClientSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentClient::class.java)) {

    @Entity(name="Client")
    class PersistentClient(

        @Column(name = "linear_id")
        public val linearID: UUID,


        @Column(name = "phoneNumber")
        public val phoneNumber: String,

        @Column(name = "FIO")
        private val FIO: String,

        @Column(name = "password")
        public val password: String
    ) : PersistentState() {
        constructor(): this(UUID.randomUUID(),
                "", "", "")
    }
}