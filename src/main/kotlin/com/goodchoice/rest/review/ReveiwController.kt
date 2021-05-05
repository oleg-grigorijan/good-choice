package com.goodchoice.rest.review

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.review.model.ReviewModificationRequest
import com.goodchoice.domain.review.service.ReviewService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviews")
@Tag(name = "review")
class ReveiwController(private val reviewService: ReviewService) {
//
//        @GetMapping("/{id}")
//        @ResponseStatus(HttpStatus.OK)
//        @Operation(summary = "Get review by id")
//        fun getById(@PathVariable id: UUID): Subject {  }
//
//        @PutMapping("/{id}")
//        @ResponseStatus(HttpStatus.NO_CONTENT)
//        @RequireSecurity
//        @Operation(summary = "Update review")
//        fun update(@PathVariable id: UUID, @RequestBody request: SubjectModificationRequest) {  }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequireSecurity
    @Operation(summary = "Create a new review")
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