package com.github.buracc.runeliteupdatepoller.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "poller")
data class PollerProperties(
    val mavenRepo: MavenRepo,
    val bootstrap: Bootstrap
) {
    @ConstructorBinding
    data class MavenRepo(
        val cron: String,
        val url: String
    )

    @ConstructorBinding
    data class Bootstrap(
        val cron: String,
        val url: String
    )
}