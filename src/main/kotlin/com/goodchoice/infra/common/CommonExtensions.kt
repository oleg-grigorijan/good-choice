package com.goodchoice.infra.common

import java.time.Clock
import java.time.LocalDateTime

fun Clock.now(): LocalDateTime = LocalDateTime.now(this)
