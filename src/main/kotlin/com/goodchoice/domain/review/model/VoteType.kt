package com.goodchoice.domain.review.model

import com.goodchoice.domain.common.jooq.enums.VoteType as JooqVoteType

enum class VoteType {
    UP, DOWN
}

fun VoteType.asJooqVoteType(): JooqVoteType {
    return when (this) {
        VoteType.DOWN -> JooqVoteType.DOWN
        VoteType.UP -> JooqVoteType.UP
    }
}

fun JooqVoteType.asVoteType(): VoteType {
    return when (this) {
        JooqVoteType.DOWN -> VoteType.DOWN
        JooqVoteType.UP -> VoteType.UP
    }
}