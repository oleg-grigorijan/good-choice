package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.EmployeeInvitation
import com.goodchoice.domain.user.model.EmployeeInvitationRequest
import com.goodchoice.domain.user.service.EmployeeInvitationService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/employees")
class EmployeeController(private val invitations: EmployeeInvitationService) {

    @PostMapping("/invitations")
    fun invite(@RequestBody request: EmployeeInvitationRequest): EmployeeInvitation =
        invitations.invite(request)

    @GetMapping("/invitations")
    fun getAllInvitations(): List<EmployeeInvitation> =
        invitations.getAll()

    @PostMapping("/invitations/{id}")
    fun refreshInvitation(@PathVariable("id") invitationId: UUID): EmployeeInvitation =
        invitations.refresh(invitationId)

    @DeleteMapping("/invitations/{id}")
    fun revokeInvitation(@PathVariable("id") invitationId: UUID) =
        invitations.revoke(invitationId)
}
