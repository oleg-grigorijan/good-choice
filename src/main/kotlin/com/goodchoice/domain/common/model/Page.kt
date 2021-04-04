package com.goodchoice.domain.common.model

import com.goodchoice.domain.common.PageNumberNegativeException

data class Page<T>(
    val offset: Int,
    val items: List<T>,
    val hasNext: Boolean
) {
    init {
        if (offset < 0)
            throw PageNumberNegativeException()
    }

    var limit: Int = items.size
}