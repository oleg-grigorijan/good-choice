package com.goodchoice.infra.swagger

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import kotlin.annotation.AnnotationTarget.*

@Target(FUNCTION, TYPE, CLASS, ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SecurityRequirements(SecurityRequirement(name = "basic-auth"))
annotation class RequireSecurity
