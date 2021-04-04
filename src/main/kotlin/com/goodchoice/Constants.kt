package com.goodchoice

import java.time.Duration

object StringMaxLength {
    const val MEDIUM = 64
    const val LARGE = 128
    const val EXTRA_LARGE = 256
}

const val EMAIL_MAX_LENGTH = 320
const val PASSWORD_MIN_LENGTH = 8

val EMPLOYEE_INVITATION_TIME_TO_LIVE: Duration = Duration.ofDays(1)
