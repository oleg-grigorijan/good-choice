package com.goodchoice.rest.image

import com.goodchoice.domain.common.model.Reference
import com.goodchoice.domain.image.model.ImageUploadingRequest
import com.goodchoice.domain.image.service.ImageService
import com.goodchoice.infra.swagger.RequireSecurity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*


@RestController
@Tag(name = "image")
@RequestMapping("/images")
class ImageController(private val imageService: ImageService) {

    @PostMapping
    @ResponseStatus(CREATED)
    @RequireSecurity
    @Operation(summary = "Add image url to database")
    fun add(@RequestBody request: ImageUploadingRequest): Reference = imageService.add(request)
}
