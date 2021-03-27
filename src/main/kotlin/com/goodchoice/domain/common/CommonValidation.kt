package com.goodchoice.domain.common

fun verify(condition: Boolean, exceptionSupplier: () -> Exception) {
    if (!condition) throw exceptionSupplier()
}

fun forbid(condition: Boolean, exceptionSupplier: () -> Exception) {
    if (condition) throw exceptionSupplier()
}

fun validateSingleLineString(string: String, maxLength: Int? = null) {
    forbid(string.contains("\n")) { StringSingleLineException(string) }
    verify(string == string.trim()) { StringTrimException(string) }
    forbid(string.contains("\\s\\s".toRegex())) { StringNormalizationException(string) }
    if (maxLength != null) {
        verify(string.length <= maxLength) { StringMaxLengthException(string, maxLength = maxLength) }
    }
}
