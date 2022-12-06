package com.github.buracc.runeliteupdatepoller.scheduled

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.model.MavenMetadata
import com.github.buracc.runeliteupdatepoller.repository.ArtifactRepository
import com.github.buracc.runeliteupdatepoller.repository.entities.Artifact
import com.github.buracc.runeliteupdatepoller.service.WebhookService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URL

@Component
class MavenRepoPoller(
    private val mapper: XmlMapper,
    private val properties: PollerProperties,
    private val repository: ArtifactRepository,
    private val webhookService: WebhookService
) {
    @Scheduled(cron = "\${poller.cron}")
    fun pollRepoAndStoreArtifact() {
        val metadata = mapper.readValue(
            URL("${properties.url}/maven-metadata.xml"),
            MavenMetadata::class.java
        ) ?: error("Metadata was null")
        val versions = metadata.versioning.versions ?: error("Versions was null")

        // Releases
        val latestRelease = versions.getLatestRelease()
        val fileName = "client-patch-${latestRelease}.jar"
        val releaseUrl = URL("${properties.url}/${versions.getLatestRelease()}/$fileName")
        val releaseArtifact = Artifact(
            fileName = fileName,
            version = latestRelease,
            originUrl = releaseUrl,
            data = releaseUrl.readBytes()
        )

        if (repository.isNewer(releaseArtifact)) {
            repository.save(releaseArtifact)
            webhookService.push(releaseArtifact)
        }

        // Snapshots
        val snapshotMetadata = mapper.readValue(
            URL("${properties.url}/${versions.getLatest()}/maven-metadata.xml"),
            MavenMetadata::class.java
        )
        val latestSnapshot = snapshotMetadata.versioning.snapshotVersions?.getLatest()
            ?: error("Snapshot version could not be determined")
        val snapshotFileName = "${snapshotMetadata.artifactId}-${latestSnapshot.value}.jar"
        val snapshotUrl = URL("${properties.url}/${snapshotMetadata.version}/$snapshotFileName")
        val snapshotArtifact = Artifact(
            fileName = snapshotFileName,
            version = snapshotMetadata.version ?: "",
            originUrl = snapshotUrl,
            data = snapshotUrl.readBytes()
        )

        if (repository.isNewer(snapshotArtifact)) {
            repository.save(snapshotArtifact)
            webhookService.push(snapshotArtifact)
        }
    }

    @Scheduled(cron = "\${poller.cron}")
    fun pollVanillaRepo() {
        val metadata = mapper.readValue(
            URL("${properties.url}/maven-metadata.xml"),
            MavenMetadata::class.java
        ) ?: error("Metadata was null")
        val versions = metadata.versioning.versions ?: error("Versions was null")

        val latest = versions.getLatest()
        val fileName = "vanilla-${latest}.jar"
        val url = URL("${properties.vanillaUrl}/$latest/$fileName")
        val artifact = Artifact(
            fileName = fileName,
            version = latest,
            originUrl = url,
            data = url.readBytes()
        )

        if (repository.isNewer(artifact)) {
            repository.save(artifact)
            webhookService.push(artifact)
        }
    }
}