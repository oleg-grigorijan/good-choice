package com.goodchoice.domain.common.exception

abstract class ApplicationException(
    val messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)
