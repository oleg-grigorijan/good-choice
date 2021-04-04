package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageOffsetNegativeException
import com.goodchoice.domain.common.verify

data class Page<T>(
    val offset: Int,
    val items: List<T>,
    val hasNext: Boolean
) {
    init {
        verify(offset < 0) { PageOffsetNegativeException() }
    }

    val limit: Int
        get() = items.size
}