package com.goodchoice.domain.brand.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.validateSingleLineString
import java.util.*

data class Brand(
    val id: UUID,
    val name: String,
    val description: String
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}