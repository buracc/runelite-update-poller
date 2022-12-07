package com.github.buracc.runeliteupdatepoller.rest

import com.github.buracc.runeliteupdatepoller.scheduled.MavenRepoPoller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks")
class WebhookController(
        private val mavenRepoPoller: MavenRepoPoller
) {
    @GetMapping("/trigger")
    fun triggerScheduler() {
        mavenRepoPoller.pollVanillaRepo()
        mavenRepoPoller.pollRepoAndStoreArtifact()
    }
}