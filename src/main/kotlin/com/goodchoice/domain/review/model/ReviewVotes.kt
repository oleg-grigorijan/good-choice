package com.goodchoice.domain.review.model

import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.review.ReviewDownvotesCountOutOfBoundsException
import com.goodchoice.domain.review.ReviewUpvotesCountOutOfBoundsException

class ReviewVotes(val upvotesCount: Long, val downvotesCount: Long, val own: Vote?) {
    init {
        forbid(upvotesCount < 0) { ReviewUpvotesCountOutOfBoundsException(upvotesCount) }
        forbid(downvotesCount < 0) { ReviewDownvotesCountOutOfBoundsException(downvotesCount) }
    }
}