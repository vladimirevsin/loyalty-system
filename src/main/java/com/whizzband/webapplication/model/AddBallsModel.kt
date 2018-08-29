package com.whizzband.webapplication.model

import net.corda.core.identity.CordaX500Name
import java.util.*

data class AddBallsModel(val phoneNumber: String,
                         val value: Double,
                         val marker: Int)