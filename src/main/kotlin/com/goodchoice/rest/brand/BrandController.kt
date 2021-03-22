package com.goodchoice.rest.brand

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/")
class BrandController {

    @GetMapping("/brands")
    @ResponseStatus(OK)
    fun getAll(@RequestParam name: String?) =
        "getAll"

}