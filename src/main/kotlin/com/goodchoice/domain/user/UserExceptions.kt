package com.goodchoice.domain.user

import com.goodchoice.domain.common.ApplicationException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.bind.annotation.ResponseStatus

abstract class UserException(
    message: String? = null,
    cause: Throwable? = null
) : ApplicationException(message, cause)

@ResponseStatus(CONFLICT)
class UserExistsByEmailException(val email: String) : UserException()
