package com.goodchoice.infra.email.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "good-choice.email")
@ConstructorBinding
data class EmailProperties(val mode: Mode, val bot: Account, val help: Account) {

    enum class Mode { REAL, EMULATION }
    data class Account(val address: String, val name: String)
}
