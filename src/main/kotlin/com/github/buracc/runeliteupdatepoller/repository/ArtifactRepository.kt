package com.github.buracc.runeliteupdatepoller.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.apache.maven.artifact.versioning.ComparableVersion
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.io.File
import java.io.FileReader
import java.nio.file.Files

@Repository
class ArtifactRepository(
    private val objectMapper: ObjectMapper
) {
    private val artifactsDir = "artifacts"
    private val cacheFileName = "artifacts.json"
    private val log = LoggerFactory.getLogger(javaClass)
    val artifacts = mutableMapOf<String, Artifact>()

    fun save(artifact: Artifact) {
        try {
            saveArtifact(artifact)
            saveCacheFile()
        } catch (e: Exception) {
            log.error("Failed to save artifact", e)
        }
    }

    fun isNewer(latest: Artifact): Boolean {
        loadCacheFile()

        val current = artifacts[latest.version]
        return current == null || current < latest || current.hash != latest.hash
    }

    fun getLatest(snapshot: Boolean): Artifact? {
        return artifacts
            .filter { snapshot || !it.key.contains("-SNAPSHOT") }
            .maxByOrNull { ComparableVersion(it.key) }
            ?.value
    }

    private fun loadCacheFile() {
        val file = File(artifactsDir, cacheFileName)
        file.parentFile.mkdir()
        if (artifacts.isEmpty() && file.exists()) {
            val cache = objectMapper.readValue(
                FileReader(file),
                object : TypeReference<MutableMap<String, Artifact>>() {}
            )
            if (cache != null) {
                artifacts.putAll(cache)
            }
        }
    }

    private fun saveArtifact(artifact: Artifact) {
        val file = File(artifactsDir, artifact.fileName)
        artifacts[artifact.version] = artifact
        file.parentFile.mkdir()
        Files.write(file.toPath(), artifact.data)
    }

    private fun saveCacheFile() {
        Files.writeString(
            File(artifactsDir, cacheFileName).toPath(),
            objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    artifacts.mapValues { (_, v) -> v.toDto() }
                )
        )
    }
}