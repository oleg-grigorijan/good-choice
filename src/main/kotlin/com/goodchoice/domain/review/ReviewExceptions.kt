package com.goodchoice.domain.review

import com.goodchoice.domain.common.ApplicationException


abstract class ReviewException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class ReviewUpvotesCountOutOfBoundsException(val value: Int) : ReviewException()
class ReviewDownvotesCountOutOfBoundsException(val value: Int) : ReviewException()
class ReviewNotFoundException() : ReviewException()