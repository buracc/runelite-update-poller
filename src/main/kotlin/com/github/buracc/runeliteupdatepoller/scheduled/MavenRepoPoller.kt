package com.github.buracc.runeliteupdatepoller.scheduled

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.buracc.runeliteupdatepoller.config.properties.PollerProperties
import com.github.buracc.runeliteupdatepoller.model.MavenMetadata
import com.github.buracc.runeliteupdatepoller.repository.ArtifactRepository
import org.apache.maven.artifact.versioning.ComparableVersion
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URL

@Component
class MavenRepoPoller(
    private val mapper: XmlMapper,
    private val properties: PollerProperties,
    private val artifactRepository: ArtifactRepository
) {

    @Scheduled(cron = "\${poller.maven-repo.cron}")
    fun pollRepoAndStoreArtifact() {
        val metadata = mapper.readValue(
            URL("${properties.mavenRepo.url}/maven-metadata.xml"),
            MavenMetadata::class.java
        ) ?: error("Metadata was null")
        val versions = metadata.versioning.versions ?: error("Versions was null")

        val latestMetadata = mapper.readValue(
            URL("${properties.mavenRepo.url}/${versions.getLatest()}/maven-metadata.xml"),
            MavenMetadata::class.java
        )

        val currentVersion = currentSnapshot?.versioning?.snapshotVersions?.getLatest()?.getComparableValue()
        val latestVersion = latestMetadata.versioning.snapshotVersions?.getLatest()?.getComparableValue() ?: error("Yo wtf?")

        if (currentVersion == null || currentVersion < latestVersion) {
            currentSnapshot = latestMetadata
        }

        val latestRelease = ComparableVersion(metadata.getLatestRelease())
        if (currentRelease == null || latestRelease > currentRelease) {
            currentRelease = latestRelease
        }
    }


}