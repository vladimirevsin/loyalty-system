package com.whizzband.webapplication.model

import net.corda.core.identity.CordaX500Name

data class SaleTokenModel(val tsc: CordaX500Name,
                          val countToken: Double,
                          val identCoalition: Int)