package com.goodchoice.domain.subject

import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

abstract class SubjectException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class ReviewsCountNegativeException(val count: Int) : SubjectException()

@ResponseStatus(NOT_FOUND)
class SubjectNotFoundException() : SubjectException()