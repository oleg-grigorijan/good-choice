package com.goodchoice.domain.brand.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.validateSingleLineString

class BrandModificationRequest(
    val name: String,
    val description: String
) {
    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}