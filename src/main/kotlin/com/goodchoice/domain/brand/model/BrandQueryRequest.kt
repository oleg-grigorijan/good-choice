package com.goodchoice.domain.brand.model

data class BrandQueryRequest(
    val query: String,
    val limit: Int,
    val offset: Int
)