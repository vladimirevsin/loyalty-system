package com.template.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

object WalletSchema

object WalletSchemaV1 : MappedSchema(
        schemaFamily = WalletSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentWallet::class.java)) {

    @Entity(name="Wallet")
    class PersistentWallet(
        @Column(name = "linear_id")
        private val linearID: UUID,

        @Column(name = "walletId")
        private val walletId: UUID,

        @Column(name = "ownerId")
        public val ownerId: UUID,

        @Column(name = "allianceTokenId")
        public val allianceTokenId: Int

        ) : PersistentState() {
        constructor(): this(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0)
    }
}