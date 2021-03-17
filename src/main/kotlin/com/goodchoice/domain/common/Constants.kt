package com.goodchoice.domain.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val SMALL_STRING_MAX_LENGTH = 64
const val MEDIUM_STRING_LENGTH = 128
const val LARGE_STRING_LENGTH = 256

fun String.normalizeSingleLine() = this.trim().replace("\\s+".toRegex(), replacement = " ")

@Serializable
data class User(@SerialName("nm") val name: String)

fun main() {
    println(Json.encodeToString(listOf(User("hello"), User("world"))))
}
