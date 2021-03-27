package com.goodchoice.domain.user

import com.goodchoice.domain.common.ApplicationException

abstract class UserException(
    messageKey: String,
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(messageKey, message, cause)

class UserExistsByEmailException : UserException("")
