package com.goodchoice.domain.common

const val MEDIUM_STRING_MAX_LENGTH = 64
const val LARGE_STRING_MAX_LENGTH = 128
const val EXTRA_LARGE_STRING_MAX_LENGTH = 256

fun String.normalizeSingleLine() = this.trim().replace("\\s+".toRegex(), replacement = " ")
fun String.isMediumString() = this.length <= MEDIUM_STRING_MAX_LENGTH
fun String.isLargeString() = this.length <= LARGE_STRING_MAX_LENGTH
fun String.isExtraLargeString() = this.length <= EXTRA_LARGE_STRING_MAX_LENGTH
