package com.goodchoice.rest.subject

import com.goodchoice.domain.subject.model.Subject
import com.goodchoice.domain.subject.model.SubjectModificationRequest
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
    fun getById(@PathVariable(value = "id") id: UUID): Subject {
        return brandService.getById(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new subject")
    fun create(@RequestBody request: SubjectModificationRequest): Subject {
        return brandService.create(request)
    }

}