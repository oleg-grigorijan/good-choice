package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageOffsetNegativeException

data class Page<T>(
    val offset: Int,
    val items: List<T>,
    val hasNext: Boolean
) {
    init {
        if (offset < 0)
            throw PageOffsetNegativeException()
    }

    val limit: Int = items.size
}