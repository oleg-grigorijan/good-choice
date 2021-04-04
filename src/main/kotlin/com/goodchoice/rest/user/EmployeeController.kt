package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.EmployeeInvitation
import com.goodchoice.domain.user.model.EmployeeInvitationRequest
import com.goodchoice.domain.user.service.EmployeeInvitationService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/employees")
@Tag(name = "employee")
@RequireSecurity
class EmployeeController(private val invitations: EmployeeInvitationService) {

    @PostMapping("/invitations")
    @Operation(summary = "Create invitation, send confirmation email")
    fun invite(@RequestBody request: EmployeeInvitationRequest): EmployeeInvitation =
        invitations.invite(request)

    @GetMapping("/invitations")
    @Operation(summary = "Get all invitations")
    fun getAllInvitations(): List<EmployeeInvitation> =
        invitations.getAll()

    @PostMapping("/invitations/{id}")
    @Operation(summary = "Extend invitation expiration, send confirmation email")
    fun refreshInvitation(@PathVariable("id") invitationId: UUID): EmployeeInvitation =
        invitations.refresh(invitationId)

    @DeleteMapping("/invitations/{id}")
    @Operation(summary = "Revoke invitation")
    fun revokeInvitation(@PathVariable("id") invitationId: UUID) =
        invitations.revoke(invitationId)
}
