package com.goodchoice.rest.reviewer

import com.goodchoice.domain.user.model.ReviewerRegistrationRequest
import com.goodchoice.domain.user.service.ReviewerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reviewers")
@Tag(name = "reviewer")
class ReviewerController(private val reviewerService: ReviewerService) {

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Register reviewer")
    fun register(@RequestBody request: ReviewerRegistrationRequest) = reviewerService.register(request)
}
