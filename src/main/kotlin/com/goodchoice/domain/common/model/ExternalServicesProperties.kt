package com.goodchoice.domain.common.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("good-choice.external-services")
@ConstructorBinding
data class ExternalServicesProperties(val webFrontend: String)
