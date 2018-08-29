package com.whizzband.webapplication.model

import java.util.*

class RegAlliance(val idAlliance: UUID,
                  val allianceName: String = "",
                  val exchangeRate: Double = 1.0,
                  val percentageAmount: Double,
                  val minAmount: Double)