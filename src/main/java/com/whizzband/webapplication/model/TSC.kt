package com.whizzband.webapplication.model

import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import java.util.*

data class TSC(val partnerId: UUID,
               val partnerParty: CordaX500Name,
               val partnerName: String,
               val partnerInn: String,
               val partnerAddInfo: String,
               val allianceId: Int,
               val walletId: UUID,
               val exchangeRate: Double,
               val percentageAmount: Double,
               val minAmount: Double)