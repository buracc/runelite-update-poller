package com.github.buracc.runeliteupdatepoller.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.apache.maven.artifact.versioning.ComparableVersion
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path

@Repository
class ArtifactRepository(
    private val objectMapper: ObjectMapper
) {
    private val cacheFilePath = "artifacts.json"
    private val log = LoggerFactory.getLogger(javaClass)
    val artifacts = mutableMapOf<String, Artifact>()

    fun save(artifact: Artifact) {
        try {
            artifacts[artifact.version] = artifact
            Files.write(Path.of(artifact.fileName), artifact.data)
            Files.writeString(Path.of(cacheFilePath), objectMapper.writeValueAsString(artifacts))
        } catch (e: Exception) {
            log.error("Failed to save artifact", e)
        }
    }

    fun isNewer(latest: Artifact): Boolean {
        if (artifacts.isEmpty() && Files.exists(Path.of(cacheFilePath))) {
            val cache = objectMapper.readValue(
                FileReader(cacheFilePath),
                object : TypeReference<MutableMap<String, Artifact>>() {}
            )
            if (cache != null) {
                artifacts.putAll(cache)
            }
        }

        val current = artifacts[latest.version]
        return current == null || current < latest || current.hash != latest.hash
    }

    fun getLatest(snapshot: Boolean): Artifact? {
        return artifacts
            .filter { snapshot || !it.key.contains("-SNAPSHOT") }
            .maxByOrNull { ComparableVersion(it.key) }
            ?.value
    }
}