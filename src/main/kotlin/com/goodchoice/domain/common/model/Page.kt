package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageOffsetNegativeException
import com.goodchoice.domain.common.forbid

data class Page<T>(
    val offset: Int,
    val items: List<T>,
    val hasNext: Boolean
) {
    init {
        forbid(offset < 0) { PageOffsetNegativeException(offset) }
    }

    val limit: Int
        get() = items.size
}