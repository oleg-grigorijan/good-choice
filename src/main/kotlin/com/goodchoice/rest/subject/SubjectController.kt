package com.goodchoice.rest.subject

import com.goodchoice.domain.subject.service.SubjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subjects")
@Tag(name = "subject")
class SubjectController(private val subjectService: SubjectService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Add a new subject")
    fun createSubject(@RequestBody request: BrandModificationRequest): BrandCreationResponse {
        return BrandCreationResponse(brandService.create(request))
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get brand by id")
    fun getById(@PathVariable(value = "id") id: UUID): Brand {
        return brandService.getById(id)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirements
    @Operation(summary = "Editing brad")
    fun edit(
        @PathVariable(value = "id") id: UUID,
        @RequestBody request: BrandModificationRequest
    ) {
        brandService.edit(id, request)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get brand previews by query")
    fun getPreviewsByQuery(
        @RequestParam query: String,
        @RequestParam limit: Int,
        @RequestParam offset: Int,
    ): List<BrandPreview> {
        return brandService.getPreviewsByQuery(BrandQueryRequest(query, limit, offset))
    }
}