package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageLimitNegativeException
import com.goodchoice.domain.common.PageOffsetNegativeException
import com.goodchoice.domain.common.forbid

data class PageRequest(
    val offset: Int,
    val limit: Int
) {
    init {
        forbid(offset < 0) { PageOffsetNegativeException(offset) }
        forbid(limit < 0) { PageLimitNegativeException(limit) }
    }
}
