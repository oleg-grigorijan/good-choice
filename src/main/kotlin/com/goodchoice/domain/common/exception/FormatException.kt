package com.goodchoice.domain.common.exception

abstract class FormatException(
    messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(messageKey, message, cause)
