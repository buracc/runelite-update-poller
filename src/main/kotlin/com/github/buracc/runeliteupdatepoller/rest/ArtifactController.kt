package com.github.buracc.runeliteupdatepoller.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/artifacts")
class ArtifactController {
    @GetMapping
    fun getLatest() {

    }
}