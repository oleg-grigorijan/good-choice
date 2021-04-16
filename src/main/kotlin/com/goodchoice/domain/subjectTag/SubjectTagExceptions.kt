package com.goodchoice.domain.subjectTag

import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class SubjectTagExceptions(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)


@ResponseStatus(HttpStatus.NOT_FOUND)
class SubjectTagNotFoundException() : SubjectTagExceptions()