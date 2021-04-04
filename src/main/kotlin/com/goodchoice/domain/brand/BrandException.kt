package com.goodchoice.domain.brand

import com.goodchoice.domain.common.ApplicationException

abstract class BrandException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

class BrandNotFoundException() : BrandException()