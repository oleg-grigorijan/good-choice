package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.EmployeeInvitationSelfView
import com.goodchoice.domain.user.model.EmployeeRegistrationRequest
import com.goodchoice.domain.user.model.toSelfView
import com.goodchoice.domain.user.persistence.EmployeeRepository
import org.springframework.transaction.annotation.Transactional

interface EmployeeSelfService {

    fun getInvitationByToken(token: String): EmployeeInvitationSelfView

    fun acceptInvitation(request: EmployeeRegistrationRequest)
}

class EmployeeSelfServiceImpl(
    private val employeeRepo: EmployeeRepository,
    private val invitationService: EmployeeInvitationService,
    private val authService: AuthService,
) : EmployeeSelfService {

    @Transactional(readOnly = true)
    override fun getInvitationByToken(token: String): EmployeeInvitationSelfView =
        invitationService.getNotExpiredByToken(token).toSelfView()

    @Transactional
    override fun acceptInvitation(request: EmployeeRegistrationRequest) {
        val invitation = invitationService.getNotExpiredByToken(request.invitationToken)
        employeeRepo.create(
            role = invitation.role,
            email = invitation.email,
            firstName = request.firstName,
            lastName = request.lastName,
            passwordHash = authService.generatePasswordHash(request.password)
        )
        invitationService.closeByToken(request.invitationToken)
    }
}
