package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageLimitNegativeException
import com.goodchoice.domain.common.PageOffsetNegativeException
import com.goodchoice.domain.common.verify

data class PageRequest(
    val offset: Int,
    val limit: Int
) {
    init {
        verify(offset < 0) { PageOffsetNegativeException() }
        verify(limit < 0) { PageLimitNegativeException() }
    }
}
