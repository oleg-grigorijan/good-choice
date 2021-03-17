package com.goodchoice.infra.common

fun verify(value: Boolean, exceptionSupplier: () -> Exception) {
    if (!value) throw exceptionSupplier()
}

fun forbid(value: Boolean, exceptionSupplier: () -> Exception) {
    if (value) throw exceptionSupplier()
}
