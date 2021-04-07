package com.goodchoice.infra.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSONB

inline fun <reified T> JSONB.read(objectMapper: ObjectMapper): T = objectMapper.readValue(this.data())
inline fun <reified T> JSONB.readList(objectMapper: ObjectMapper): List<T> = objectMapper.readValue(this.data())
inline fun <reified K, reified V> JSONB.readMap(objectMapper: ObjectMapper): Map<K, V> =
    objectMapper.readValue(this.data())