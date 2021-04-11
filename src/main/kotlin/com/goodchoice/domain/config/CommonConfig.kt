package com.goodchoice.domain.config

import com.goodchoice.domain.common.model.ExternalServicesProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ExternalServicesProperties::class)
class CommonConfig
