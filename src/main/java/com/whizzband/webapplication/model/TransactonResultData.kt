package com.whizzband.webapplication.model

import enums.EnumError
import java.util.*

data class TransactonResultData(val listWallet: Wallet, override val code: Int, override val message: String) : ResultData(code, message)

data class Wallet(val walletId: UUID, val value: Double, val marker: Int)