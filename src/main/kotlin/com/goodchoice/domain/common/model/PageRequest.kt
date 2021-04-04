package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageNumberNegativeException
import com.goodchoice.domain.common.PageSizeNegativeException

data class PageRequest(
    val offset: Int,
    val limit: Int
) {
    init {
        if (offset < 0)
            throw PageNumberNegativeException()

        if (limit < 0)
            throw PageSizeNegativeException()
    }
}
