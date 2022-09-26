package com.github.buracc.runeliteupdatepoller.config

import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(PollerProperties::class)
@EnableScheduling
class PollerConfig {
    @Bean
    fun restTemplate() = RestTemplate()
}