package com.goodchoice.domain.subject

import com.goodchoice.domain.common.ApplicationException

abstract class SubjectException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class ReviewsCountNegativeException(val count: Int) : SubjectException()