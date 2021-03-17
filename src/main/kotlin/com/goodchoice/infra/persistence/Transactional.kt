package com.goodchoice.infra.persistence

import org.springframework.core.annotation.AliasFor
import java.lang.annotation.Inherited
import org.springframework.transaction.annotation.Transactional as SpringTransactional

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@SpringTransactional(rollbackFor = [Exception::class])
annotation class Transactional(@get:AliasFor(annotation = SpringTransactional::class, attribute = "readOnly") val readOnly: Boolean = false)
