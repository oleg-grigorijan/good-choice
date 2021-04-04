package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.EmployeeInvitationSelfView
import com.goodchoice.domain.user.model.EmployeeRegistrationRequest
import com.goodchoice.domain.user.service.EmployeeSelfService
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employees/self")
class EmployeeSelfController(private val employeeSelfService: EmployeeSelfService) {

    @GetMapping("/invitations")
    fun getInvitationByToken(@RequestParam token: String): EmployeeInvitationSelfView =
        employeeSelfService.getInvitationByToken(token)

    @PostMapping
    @SecurityRequirements
    fun acceptInvitation(@RequestBody request: EmployeeRegistrationRequest) {
        employeeSelfService.acceptInvitation(request)
    }
}
