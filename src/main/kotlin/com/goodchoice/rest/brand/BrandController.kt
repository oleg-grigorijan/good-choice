package com.goodchoice.rest.brand

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/brands")
@Tag(name = "brand")
class BrandController {

//    @GetMapping()
//    @ResponseStatus(OK)
//    fun getAll(@RequestParam name: String?) {
//
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    @Operation(summary = "Add a new brand")
    fun addBrand() {

    }

}