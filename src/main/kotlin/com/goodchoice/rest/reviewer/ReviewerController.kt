package com.goodchoice.rest.reviewer

import com.goodchoice.domain.user.model.ReviewerRegistrationRequest
import com.goodchoice.domain.user.service.ReviewerService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reviewers")
class ReviewerController(private val reviewerService: ReviewerService) {

    @PostMapping
    fun register(@RequestBody request: ReviewerRegistrationRequest) = reviewerService.register(request)
}
