package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageLimitNegativeException
import com.goodchoice.domain.common.PageOffsetNegativeException

data class PageRequest(
    val offset: Int,
    val limit: Int
) {
    init {
        if (offset < 0)
            throw PageOffsetNegativeException()

        if (limit < 0)
            throw PageLimitNegativeException()
    }
}
