package com.goodchoice.domain.review.model

enum class VoteType {
    UP, DOWN
}

fun VoteType.asJooqVoteType(): com.goodchoice.domain.common.jooq.enums.VoteType {
    return when (this) {
        VoteType.DOWN -> com.goodchoice.domain.common.jooq.enums.VoteType.DOWN
        VoteType.UP -> com.goodchoice.domain.common.jooq.enums.VoteType.UP
    }
}

fun com.goodchoice.domain.common.jooq.enums.VoteType.asVoteType(): VoteType {
    return when (this) {
        com.goodchoice.domain.common.jooq.enums.VoteType.DOWN -> VoteType.DOWN
        com.goodchoice.domain.common.jooq.enums.VoteType.UP -> VoteType.UP
    }
}