package com.goodchoice.rest.subject

import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subject.model.*
import com.goodchoice.domain.subject.service.SubjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/subjects")
@Tag(name = "subject")
class SubjectController(private val subjectService: SubjectService) {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get subject by id")
    fun getById(@PathVariable(value = "id") id: UUID): Subject =
        subjectService.getById(id)

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update subject")
    fun update(@PathVariable(value = "id") id: UUID, @RequestBody request: SubjectModificationRequest) {
        subjectService.update(id, request)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new subject")
    fun create(@RequestBody request: SubjectCreationRequest): Reference =
        subjectService.create(request)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get subject previews by query")
    fun getAllPreviewsByQuery(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) brandId: UUID?,
        @RequestParam(required = false) tagId: UUID?,
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): Page<SubjectPreview> =
        subjectService.getAllPreviewsByQuery(
            subjectQuery = SubjectQuery(query = query, brandId = brandId, subjectTagId = tagId),
            pageRequest = PageRequest(offset = offset, limit = limit)
        )

}