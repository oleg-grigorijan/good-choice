package com.goodchoice.domain.user.service

import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.user.model.EmployeeInvitationSelfView
import com.goodchoice.domain.user.model.EmployeeRegistrationRequest
import com.goodchoice.domain.user.persistence.EmployeeRepository

interface EmployeeSelfService {

    fun getInvitationByToken(token: String): EmployeeInvitationSelfView

    fun acceptInvitation(request: EmployeeRegistrationRequest)
}

class EmployeeSelfServiceImpl(
    private val employeeRepo: EmployeeRepository,
    private val invitations: EmployeeInvitationService,
    private val authService: AuthService,
) : EmployeeSelfService {

    override fun getInvitationByToken(token: String): EmployeeInvitationSelfView =
        invitations.getNotExpiredByToken(token)

    override fun acceptInvitation(request: EmployeeRegistrationRequest) {
        val invitation = invitations.getNotExpiredByToken(request.invitationToken)
        employeeRepo.create(
            email = invitation.email,
            firstName = request.firstName,
            lastName = request.lastName,
            passwordHash = authService.generatePasswordHash(request.password)
        )
        invitations.closeByToken(request.invitationToken)
    }
}
