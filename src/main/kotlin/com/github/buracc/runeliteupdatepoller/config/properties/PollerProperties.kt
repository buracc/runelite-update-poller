package com.github.buracc.runeliteupdatepoller.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "poller")
data class PollerProperties(
    val cron: String,
    val url: String,
    val webhookUrl: String
)