package com.goodchoice.domain.review

import com.goodchoice.domain.common.ApplicationException


abstract class ReviewException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class ReviewUpvotesCountOutOfBoundsException(val value: Long) : ReviewException()
class ReviewDownvotesCountOutOfBoundsException(val value: Long) : ReviewException()
class ReviewNotFoundException() : ReviewException()