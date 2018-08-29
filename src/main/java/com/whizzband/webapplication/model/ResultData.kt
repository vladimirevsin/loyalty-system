package com.whizzband.webapplication.model

import enums.EnumError

open class ResultData(open val code: Int = -1, open val message: String = "") {
    constructor(enumError: EnumError) : this(enumError.code, enumError.message)
}