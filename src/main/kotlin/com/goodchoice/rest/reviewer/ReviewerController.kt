package com.goodchoice.rest.reviewer

import com.goodchoice.domain.user.model.ReviewerRegistrationRequest
import com.goodchoice.domain.user.service.ReviewerSelfService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviewers")
@Tag(name = "reviewer")
class ReviewerController(private val reviewerSelfService: ReviewerSelfService) {

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Register reviewer")
    fun register(@RequestBody request: ReviewerRegistrationRequest) = reviewerSelfService.register(request)
}
