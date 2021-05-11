package com.goodchoice.rest.review

import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.*
import com.goodchoice.domain.review.service.ReviewService
import com.goodchoice.domain.subject.model.Mark
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "review")
class ReviewController(private val reviewService: ReviewService) {

    @PutMapping("/reviews/{id}/votes/own")
    @ResponseStatus(HttpStatus.OK)
    @RequireSecurity
    @Operation(summary = "Vote by authenticated user")
    fun voteByAuthenticatedUser(@PathVariable id: UUID, @RequestBody request: Vote): ReviewVotes =
        reviewService.voteByAuthenticatedUser(id, request)

    @DeleteMapping("/reviews/{id}/votes/own")
    @ResponseStatus(HttpStatus.OK)
    @RequireSecurity
    @Operation(summary = "Remove vote of authenticated user")
    fun removeAuthenticatedUserVote(@PathVariable id: UUID): ReviewVotes =
        reviewService.removeAuthenticatedUserVote(id)

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @RequireSecurity
    @Operation(summary = "Create a new review by authenticated user")
    fun create(@RequestBody request: ReviewCreationRequest): Reference = reviewService.create(request)

    @GetMapping("/subjects/{id}/reviews")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get reviews by subject",
        description = "When user is not authenticated returns null in votes.own for all items."
    )
    fun getAllBySubject(
        @PathVariable id: UUID,
        @RequestParam(required = false) mark: Int?,
        @RequestParam(required = false) filterNotOwn: Boolean?,
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): Page<Review> =
        reviewService.getAllBySubject(
            reviewBySubjectQuery = ReviewBySubjectQuery(id, filterNotOwn ?: false, mark?.let { Mark(mark) }),
            pageRequest = PageRequest(offset = offset, limit = limit)
        )

    @GetMapping("/subjects/{id}/reviews/own")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get review by subject and authenticated author")
    fun getBySubjectAndAuthenticatedAuthor(
        @PathVariable id: UUID
    ): Review =
        reviewService.getOwnBySubject(subject = Reference(id))
}