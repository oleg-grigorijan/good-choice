package com.goodchoice.infra.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSONB

inline fun <reified T> ObjectMapper.read(value: JSONB): T = this.readValue(value.data())

