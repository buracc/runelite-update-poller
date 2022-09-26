package com.github.buracc.runeliteupdatepoller.mapping

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import java.io.File
import java.net.URL
import java.nio.file.Files

class ArtifactDeserializer(
    private val pollerProperties: PollerProperties
) : JsonDeserializer<Artifact>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Artifact {
        val node = parser.codec.readTree<JsonNode>(parser)
        val fileName = node.get("fileName").textValue()
        val version = node.get("version").textValue()
        val url = node.get("url").textValue()
        return Artifact(
            fileName = fileName,
            version = version,
            originUrl = URL(url),
            data = Files.readAllBytes(File(pollerProperties.cacheDirectory, fileName).toPath())
        )
    }
}