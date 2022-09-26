package com.github.buracc.runeliteupdatepoller.repository

import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Path

@Repository
class ArtifactRepository {
    private val log = LoggerFactory.getLogger(javaClass)
    private val artifacts = mutableMapOf<String, Artifact>()

    fun save(artifact: Artifact) {
        try {
            artifacts[artifact.fileName] = artifact
            Files.write(Path.of(artifact.fileName), artifact.data)
        } catch (e: Exception) {
            log.error("Failed to save artifact", e)
        }
    }

    fun isNewer(artifact: Artifact): Boolean {
        val current = artifacts.putIfAbsent(artifact.fileName, artifact) ?: error("")
        return current < artifact
    }
}