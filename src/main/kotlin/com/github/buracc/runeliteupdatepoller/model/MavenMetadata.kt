package com.github.buracc.runeliteupdatepoller.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.apache.maven.artifact.versioning.ComparableVersion

@JacksonXmlRootElement(localName = "metadata")
data class MavenMetadata(
    val groupId: String,
    val artifactId: String,
    val versioning: Versioning,
    val version: String?
) {
    data class Versioning(
        val release: String?,
        val versions: Versions?,
        val lastUpdated: String,
        val snapshot: Snapshot?,
        val snapshotVersions: SnapshotVersions?
    ) {
        data class Versions(
            val version: List<String>
        ) {
            fun getLatest(): String {
                return version.maxByOrNull { ComparableVersion(it) } ?: error("Weird")
            }

            fun getLatestRelease(): String {
                return version.filter { !it.contains("-SNAPSHOT") }
                    .maxByOrNull { ComparableVersion(it) } ?: error("Weird")
            }
        }

        data class Snapshot(
            val timeStamp: String,
            val buildNumber: Int
        )

        data class SnapshotVersions(
            val snapshotVersion: List<SnapshotVersion>?
        ) {
            fun getLatest(): SnapshotVersion {
                return snapshotVersion?.maxByOrNull { ComparableVersion(it.value) } ?: error("Weird")
            }
        }

        data class SnapshotVersion(
            val extension: String,
            val value: String,
            val updated: String
        ) {
            fun getComparableValue() = ComparableVersion(value)
        }
    }
}