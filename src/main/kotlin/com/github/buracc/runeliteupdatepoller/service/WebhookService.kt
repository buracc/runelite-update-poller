package com.github.buracc.runeliteupdatepoller.service

import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import com.github.buracc.runeliteupdatepoller.rest.dto.ArtifactDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.net.URISyntaxException

@Service
class WebhookService(
    private val restTemplate: RestTemplate,
    private val properties: PollerProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun push(artifact: Artifact) {
        try {
            val httpEntity = HttpEntity<ArtifactDto>(artifact.toDto())
            httpEntity.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            restTemplate.exchange(
                URI(properties.webhookUrl),
                HttpMethod.POST,
                httpEntity,
                Any::class.java
            )
        } catch (e: RestClientException) {
            log.error("Failed to push to ${properties.webhookUrl}", e)
        } catch (e: URISyntaxException) {
            log.error("Invalid webhook URL", e)
        }
    }
}