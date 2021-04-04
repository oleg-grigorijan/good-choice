package com.goodchoice.infra.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration


@Configuration
@OpenAPIDefinition(
    info = Info(title = "Good Choice API"),
    security = []
)
@SecurityScheme(
    name = "basic-auth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
class SwaggerConfig
