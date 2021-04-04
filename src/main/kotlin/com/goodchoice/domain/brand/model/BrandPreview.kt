package com.goodchoice.domain.brand.model

import com.goodchoice.StringMaxLength
import com.goodchoice.domain.common.validateSingleLineString
import java.util.*

data class BrandPreview(
    val id: UUID,
    val name: String,
) {

    init {
        validateSingleLineString(name, StringMaxLength.MEDIUM)
    }
}