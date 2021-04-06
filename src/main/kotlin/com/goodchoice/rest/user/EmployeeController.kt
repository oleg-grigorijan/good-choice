package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.EmployeeInvitation
import com.goodchoice.domain.user.model.EmployeeInvitationRequest
import com.goodchoice.domain.user.service.EmployeeInvitationService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/employees")
@RequireSecurity
@Tag(name = "employee")
class EmployeeController(private val invitationService: EmployeeInvitationService) {

    @PostMapping("/invitations")
    @ResponseStatus(CREATED)
    @Operation(summary = "Create invitation, send confirmation email")
    fun invite(@RequestBody request: EmployeeInvitationRequest): EmployeeInvitation =
        invitationService.invite(request)

    @GetMapping("/invitations")
    @ResponseStatus(OK)
    @Operation(summary = "Get all invitations")
    fun getAllInvitations(): List<EmployeeInvitation> =
        invitationService.getAll()

    @PostMapping("/invitations/{id}")
    @ResponseStatus(OK)
    @Operation(summary = "Extend invitation expiration, send confirmation email")
    fun refreshInvitation(@PathVariable("id") invitationId: UUID): EmployeeInvitation =
        invitationService.refresh(invitationId)

    @DeleteMapping("/invitations/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Revoke invitation")
    fun revokeInvitation(@PathVariable("id") invitationId: UUID) =
        invitationService.revoke(invitationId)
}
