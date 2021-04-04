package com.goodchoice.domain.user.service

import com.goodchoice.EMPLOYEE_INVITATION_TIME_TO_LIVE
import com.goodchoice.domain.auth.model.UserRole.HR
import com.goodchoice.domain.auth.model.requireRole
import com.goodchoice.domain.auth.service.AuthService
import com.goodchoice.domain.common.forbid
import com.goodchoice.domain.common.generateToken
import com.goodchoice.domain.user.EmployeeInvitationExpiredException
import com.goodchoice.domain.user.EmployeeInvitationNotFoundException
import com.goodchoice.domain.user.UserExistsByEmailException
import com.goodchoice.domain.user.model.EmployeeInvitation
import com.goodchoice.domain.user.model.EmployeeInvitationRequest
import com.goodchoice.domain.user.persistence.EmployeeInvitationRepository
import com.goodchoice.infra.email.model.EmailTemplateInput
import com.goodchoice.infra.email.service.EmailService
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime.now
import java.util.*

interface EmployeeInvitationService {

    fun getAll(): List<EmployeeInvitation>
    fun getNotExpiredByToken(token: String): EmployeeInvitation
    fun invite(request: EmployeeInvitationRequest): EmployeeInvitation
    fun refresh(id: UUID): EmployeeInvitation
    fun revoke(id: UUID)
    fun closeByToken(token: String)
}

class EmployeeInvitationServiceImpl(
    private val invitationRepo: EmployeeInvitationRepository,
    private val emailService: EmailService,
    private val authService: AuthService,
    private val clock: Clock,
) : EmployeeInvitationService {

    @Transactional(readOnly = true)
    override fun getAll(): List<EmployeeInvitation> {
        authService.currentAuth.requireRole(HR)
        return invitationRepo.getAll()
    }

    @Transactional(readOnly = true)
    override fun getNotExpiredByToken(token: String): EmployeeInvitation {
        val invitation = invitationRepo.getByTokenOrNull(token) ?: throw EmployeeInvitationNotFoundException()
        forbid(invitation.isExpired(clock)) { EmployeeInvitationExpiredException() }
        return invitation
    }

    @Transactional
    override fun invite(request: EmployeeInvitationRequest): EmployeeInvitation {
        authService.currentAuth.requireRole(HR)
        forbid(authService.existsByEmail(request.email)) { UserExistsByEmailException(request.email) }

        val token = generateToken()
        val invitation = invitationRepo.create(
            token = token,
            email = request.email,
            role = request.role,
            suggestedFirstName = request.suggestedFirstName,
            suggestedLastName = request.suggestedLastName,
            expiredTimestamp = getExpiredTimestamp()
        )

        // TODO(#8): After tx commit
        sendConfirmationEmail(invitation, token)

        return invitation
    }

    @Transactional
    override fun refresh(id: UUID): EmployeeInvitation {
        authService.currentAuth.requireRole(HR)

        val token = generateToken()
        val invitation = invitationRepo.refresh(
            id = id,
            refreshedToken = token,
            expiredTimestamp = getExpiredTimestamp()
        )

        // TODO(#8): After tx commit
        sendConfirmationEmail(invitation, token)

        return invitation
    }

    @Transactional
    override fun revoke(id: UUID) {
        authService.currentAuth.requireRole(HR)
        invitationRepo.remove(id)
    }

    @Transactional
    override fun closeByToken(token: String) {
        invitationRepo.removeByToken(token)
    }

    private fun getExpiredTimestamp() = now(clock) + EMPLOYEE_INVITATION_TIME_TO_LIVE

    private fun sendConfirmationEmail(invitation: EmployeeInvitation, token: String) {
        emailService.send(to = invitation.email, EmailTemplateInput.EmployeeInvitation(
            token = token,
            suggestedFirstName = invitation.suggestedFirstName,
            webFrontendUrl = "http://localhost:4200"
        ))
    }
}
