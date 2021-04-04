package com.goodchoice.rest.user

import com.goodchoice.domain.user.model.EmployeeInvitationSelfView
import com.goodchoice.domain.user.model.EmployeeRegistrationRequest
import com.goodchoice.domain.user.service.EmployeeSelfService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employees/self")
@Tag(name = "employee-self")
class EmployeeSelfController(private val employeeSelfService: EmployeeSelfService) {

    @GetMapping("/invitations")
    @Operation(summary = "Verify invitation exists by token, get invitation data")
    fun getInvitationByToken(@RequestParam token: String): EmployeeInvitationSelfView =
        employeeSelfService.getInvitationByToken(token)

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Accept invitation and register employee")
    fun acceptInvitation(@RequestBody request: EmployeeRegistrationRequest) {
        employeeSelfService.acceptInvitation(request)
    }
}
