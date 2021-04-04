package com.goodchoice.domain.brand

import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

abstract class BrandException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

@ResponseStatus(NOT_FOUND)
class BrandNotFoundException() : BrandException()