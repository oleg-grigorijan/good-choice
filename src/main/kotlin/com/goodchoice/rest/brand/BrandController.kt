package com.goodchoice.rest.brand

import com.goodchoice.domain.brand.model.BrandModification
import com.goodchoice.domain.brand.service.BrandService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/brands")
@Tag(name = "brand")
class BrandController(private val brandService: BrandService) {

//    @GetMapping()
//    @ResponseStatus(OK)
//    fun getAll(@RequestParam name: String?) {
//
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Add a new brand")
    fun createBrand(@RequestBody request: BrandModification) {

    }

}