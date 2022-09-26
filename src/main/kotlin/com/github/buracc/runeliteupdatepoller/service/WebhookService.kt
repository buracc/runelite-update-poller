package com.github.buracc.runeliteupdatepoller.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WebhookService(
    private val restTemplate: RestTemplate
) {
}