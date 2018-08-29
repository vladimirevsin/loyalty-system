package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object TokenPaperSchema

object TokenPaperSchemaV1 : MappedSchema(
        schemaFamily = TokenPaperSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTokenPaper::class.java)) {

    @Entity(name="token_balance")
    class PersistentTokenPaper(

        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "sender")
        private val sender: String,

        @Column(name = "recipient")
        private val pecipient: String,

        @Column(name = "value")
        private val value: Double,

        @Column(name = "marker")
        private val marker: Int,

        @Column(name = "clientSender")
        private val clientSender: UUID?,

        @Column(name = "clientRecipient")
        private val clientReciepent: UUID?

    ) : PersistentState() {
        constructor(): this(UUID.randomUUID(),
                "", "", 0.0, 0, null, null)
    }
}