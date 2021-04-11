package com.goodchoice.domain.common

import java.util.UUID.randomUUID

fun generateToken(): String = "${randomUUID()}-${randomUUID()}"
