package com.github.buracc.runeliteupdatepoller.repository

import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.io.IOException
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

    fun isNewer(latest: Artifact): Boolean {
        var current = artifacts[latest.fileName]
        if (current == null) {
            val bytes = try {
                Files.readAllBytes(Path.of(latest.fileName))
            } catch (ex: IOException) {
                null
            }

            if (bytes != null) {
                current = Artifact(
                    latest.fileName,
                    latest.version,
                    bytes
                )
            }
        }

        return current == null || current < latest || current.hash != latest.hash
    }
}