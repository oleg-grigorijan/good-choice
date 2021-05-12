package com.goodchoice.domain.user.model

import com.goodchoice.domain.auth.model.UserRole
import com.goodchoice.domain.common.UnexpectedUserRoleException
import com.goodchoice.domain.common.model.Email
import com.goodchoice.domain.common.model.RawPassword
import com.goodchoice.domain.common.verify
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("good-choice.bootstrap.employee-creation")
@ConstructorBinding
data class EmployeeCreationBootstrapProperties(
    val enabled: Boolean,
    val accounts: List<Account>,
) {

    data class Account(
        val role: UserRole,
        val email: Email,
        val firstName: String,
        val lastName: String,
        val password: RawPassword,
    ) {
        init {
            verify(role.isEmployee()) { UnexpectedUserRoleException() }
        }
    }
}
