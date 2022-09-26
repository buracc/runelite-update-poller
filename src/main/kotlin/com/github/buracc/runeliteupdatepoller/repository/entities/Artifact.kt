package com.github.buracc.runeliteupdatepoller.repository.entities

import org.apache.maven.artifact.versioning.ComparableVersion

data class Artifact(
    val fileName: String,
    val version: String?,
    val hash: String?,
    val data: ByteArray
) : Comparable<Artifact> {
    private val comparableVersion = if (version == null) null else ComparableVersion(version)

    override fun compareTo(other: Artifact): Int {
        return comparableVersion.compareTo(other.comparableVersion)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artifact

        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        return fileName.hashCode()
    }
}