package com.goodchoice.rest.brand

import com.goodchoice.domain.brand.model.*
import com.goodchoice.domain.brand.service.BrandService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
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
    @ResponseBody
    @SecurityRequirements
    @Operation(summary = "Add a new brand")
    fun createBrand(@RequestBody request: BrandModificationRequest): BrandCreationResponse {
        return BrandCreationResponse(brandService.create(request))
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @SecurityRequirements
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
    @ResponseBody
    @SecurityRequirements
    @Operation(summary = "Get brand by id")
    fun getPreviewsByQuery(
        @RequestParam query: String,
        @RequestParam limit: Int,
        @RequestParam offset: Int,
    ): List<BrandPreview> {
        return brandService.getPreviewsByQuery(BrandQueryRequest(query, limit, offset))
    }

}