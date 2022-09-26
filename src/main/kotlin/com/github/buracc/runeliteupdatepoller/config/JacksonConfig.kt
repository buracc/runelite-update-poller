package com.github.buracc.runeliteupdatepoller.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.mapping.ArtifactDeserializer
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(pollerProperties: PollerProperties) = ObjectMapper()
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        .registerModule(JavaTimeModule())
        .registerModule(SimpleModule().also { it.addDeserializer(Artifact::class.java, ArtifactDeserializer(pollerProperties)) })
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    @Bean
    fun xmlMapper() = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            as XmlMapper
}