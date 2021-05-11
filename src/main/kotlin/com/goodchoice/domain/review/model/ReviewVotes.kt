package com.goodchoice.domain.review.model

import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.review.ReviewVotesCountOutOfBoundsException

class ReviewVotes(val upvotesCount: Long, val downvotesCount: Long) {
    init {
        forbid(upvotesCount < 0) { ReviewVotesCountOutOfBoundsException(upvotesCount) }
        forbid(downvotesCount < 0) { ReviewVotesCountOutOfBoundsException(downvotesCount) }
    }
}