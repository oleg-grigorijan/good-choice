package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.EmployeeCreationBootstrapProperties
import com.goodchoice.domain.user.persistence.EmployeeRepository
import org.springframework.transaction.annotation.Transactional

class EmployeeCreationBootstrap(
        private val employeeRepo: EmployeeRepository,
        private val authService: AuthService,
        private val accounts: List<EmployeeCreationBootstrapProperties.Account>,
) : Runnable {

    @Transactional
    override fun run() {
        accounts
                .filterNot { authService.existsByEmail(it.email) }
                .forEach {
                    employeeRepo.create(
                            role = it.role,
                            email = it.email,
                            firstName = it.firstName,
                            lastName = it.lastName,
                            passwordHash = authService.generatePasswordHash(it.password)
                    )
                }
    }
}
