package com.goodchoice.rest.example

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/example")
@Tag(name = "example")
class ExampleController {

    @GetMapping("/hello")
    @ResponseStatus(OK)
    @Operation(summary = "Returns greeting")
    fun hello(@RequestParam name: String?) =
        "Hello, " + (name ?: "world")

    data class ExampleRequest(val a: String, val b: String)
    data class ExampleResponse(val id: UUID, val c: Int?, val d: Int?)

    @PutMapping("/foo/{id}")
    @ResponseStatus(OK)
    @Operation(summary = "Casts a and b to integers")
    fun foo(@PathVariable id: UUID, @RequestBody req: ExampleRequest) =
        ExampleResponse(id, req.a.toIntOrNull(), req.b.toIntOrNull())
}
