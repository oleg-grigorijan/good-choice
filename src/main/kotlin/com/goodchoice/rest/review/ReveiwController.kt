package com.goodchoice.rest.review

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.ReviewModificationRequest
import com.goodchoice.domain.review.model.ReviewVotes
import com.goodchoice.domain.review.model.Vote
import com.goodchoice.domain.review.service.ReviewService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/reviews")
@Tag(name = "review")
class ReveiwController(private val reviewService: ReviewService) {

    //        @GetMapping("/{id}")
//        @ResponseStatus(HttpStatus.OK)
//        @Operation(summary = "Get review by id")
//        fun getById(@PathVariable id: UUID): Subject {  }
//
    @PutMapping("/{id}/votes/own")
    @ResponseStatus(HttpStatus.OK)
    @RequireSecurity
    @Operation(summary = "Vote by authenticated user")
    fun voteByAuthenticatedUser(@PathVariable id: UUID, @RequestBody request: Vote): ReviewVotes =
        reviewService.voteByAuthenticatedUser(id, request)

    @DeleteMapping("/{id}/votes/own")
    @ResponseStatus(HttpStatus.OK)
    @RequireSecurity
    @Operation(summary = "Remove vote of authenticated user")
    fun removeAuthenticatedUserVote(@PathVariable id: UUID): ReviewVotes =
        reviewService.removeAuthenticatedUserVote(id)


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireSecurity
    @Operation(summary = "Create a new review by authenticated user")
    fun create(@RequestBody request: ReviewModificationRequest): Reference = reviewService.create(request)

//        @GetMapping
//        @ResponseStatus(HttpStatus.OK)
//        @Operation(summary = "Get subject previews by query")
//        fun getAllPreviewsByQuery(
//            @RequestParam(required = false) query: String?,
//            @RequestParam(required = false) brandId: UUID?,
//            @RequestParam limit: Int,
//            @RequestParam offset: Int
//        ): Page<SubjectPreview> { }

}