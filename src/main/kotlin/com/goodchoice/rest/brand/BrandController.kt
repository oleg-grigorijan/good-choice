package com.goodchoice.rest.brand

import com.goodchoice.domain.brand.model.Brand
import com.goodchoice.domain.brand.model.BrandModificationRequest
import com.goodchoice.domain.brand.model.BrandPreview
import com.goodchoice.domain.brand.service.BrandService
import com.goodchoice.domain.common.model.Page
import com.goodchoice.domain.common.model.PageRequest
import com.goodchoice.domain.common.model.Reference
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/brands")
@Tag(name = "brand")
class BrandController(private val brandService: BrandService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireSecurity
    @Operation(summary = "Add a new brand")
    fun create(@RequestBody request: BrandModificationRequest): Reference =
        brandService.create(request)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get brand by id")
    fun getById(@PathVariable id: UUID): Brand =
        brandService.getById(id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequireSecurity
    @Operation(summary = "Update brad")
    fun update(@PathVariable id: UUID, @RequestBody request: BrandModificationRequest) {
        brandService.update(id, request)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get brand previews by query")
    fun getAllPreviewsByQuery(
        @RequestParam query: String,
        @RequestParam limit: Int,
        @RequestParam offset: Int
    ): Page<BrandPreview> =
        brandService.getAllPreviewsByQuery(query = query, PageRequest(offset = offset, limit = limit))

}