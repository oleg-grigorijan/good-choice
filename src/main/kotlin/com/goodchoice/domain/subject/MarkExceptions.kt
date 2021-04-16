package com.goodchoice.domain.subject

import com.goodchoice.domain.common.FormatException

abstract class MarkException(
    message: String? = null,
    cause: Throwable? = null
) : FormatException(message, cause)

class MarkOutOfBoundsException(val value: Int) : MarkException()
class MarkCountNegativeException(val count: Int) : MarkException()
class AverageMarkOfBoundsException(val value: Double?) : MarkException()