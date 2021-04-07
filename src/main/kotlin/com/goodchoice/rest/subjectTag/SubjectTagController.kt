package com.goodchoice.rest.subjectTag

import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.subjectTag.model.SubjectTag
import com.goodchoice.domain.subjectTag.model.SubjectTagModificationRequest
import com.goodchoice.domain.subjectTag.service.SubjectTagService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/subjects/tags")
@Tag(name = "subject tags")
class SubjectTagController(private val subjectTagService: SubjectTagService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireSecurity
    @Operation(summary = "Add a new subject tag")
    fun create(@RequestBody request: SubjectTagModificationRequest): Reference =
        subjectTagService.create(request)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get subject tag by id")
    fun getById(@PathVariable id: UUID): SubjectTag =
        subjectTagService.getById(id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequireSecurity
    @Operation(summary = "Update subject tag")
    fun update(@PathVariable id: UUID, @RequestBody request: SubjectTagModificationRequest) {
        subjectTagService.update(id, request)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get subject tags by query")
    fun getAllPreviewsByQuery(
        @RequestParam query: String,
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): Page<SubjectTag> =
        subjectTagService.getAllByQuery(query = query, PageRequest(offset = offset, limit = limit))
}