package com.github.buracc.runeliteupdatepoller.scheduled

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.model.MavenMetadata
import com.github.buracc.runeliteupdatepoller.repository.ArtifactRepository
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URL

@Component
class MavenRepoPoller(
    private val mapper: XmlMapper,
    private val properties: PollerProperties,
    private val repository: ArtifactRepository
) {
    @Scheduled(cron = "\${poller.maven-repo.cron}")
    fun pollRepoAndStoreArtifact() {
        val metadata = mapper.readValue(
            URL("${properties.mavenRepo.url}/maven-metadata.xml"),
            MavenMetadata::class.java
        ) ?: error("Metadata was null")
        val versions = metadata.versioning.versions ?: error("Versions was null")

        // Releases
        val latestRelease = versions.getLatestRelease()
        val fileName = "client-patch-${latestRelease}.jar"
        val releaseArtifact = Artifact(
            fileName = fileName,
            version = latestRelease,
            data = URL("${properties.mavenRepo.url}/${versions.getLatestRelease()}/$fileName").readBytes()
        )

        if (repository.isNewer(releaseArtifact)) {
            repository.save(releaseArtifact)
        }

        // Snapshots
        val snapshotMetadata = mapper.readValue(
            URL("${properties.mavenRepo.url}/$latestRelease/maven-metadata.xml"),
            MavenMetadata::class.java
        )
        val latestSnapshot = snapshotMetadata.versioning.snapshotVersions?.getLatest()
            ?: error("Snapshot version could not be determined")
        val snapshotFileName = "${snapshotMetadata.artifactId}-${latestSnapshot.value}.jar"
        val snapshotArtifact = Artifact(
            fileName = snapshotFileName,
            version = snapshotMetadata.version ?: "",
            data = URL("${properties.mavenRepo.url}/${snapshotMetadata.version}/$snapshotFileName").readBytes()
        )

        if (repository.isNewer(snapshotArtifact)) {
            repository.save(snapshotArtifact)
        }
    }
}