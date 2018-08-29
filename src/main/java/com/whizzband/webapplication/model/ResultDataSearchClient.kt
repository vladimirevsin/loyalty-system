package com.whizzband.webapplication.model

import enums.EnumError

data class ResultDataSearchClient(override val code: Int = -1, override val message: String = "", val searchClient: Boolean)
    : ResultData(code, message)
