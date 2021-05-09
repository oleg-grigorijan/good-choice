package com.goodchoice.domain.review

import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus


abstract class ReviewException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class ReviewVotesCountOutOfBoundsException(val value: Long) : ReviewException()

@ResponseStatus(NOT_FOUND)
class ReviewNotFoundException() : ReviewException()