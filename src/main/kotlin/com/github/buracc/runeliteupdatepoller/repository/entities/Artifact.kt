package com.github.buracc.runeliteupdatepoller.repository.entities

import com.github.buracc.runeliteupdatepoller.util.Hashing
import org.apache.maven.artifact.versioning.ComparableVersion

data class Artifact(
    val fileName: String,
    val version: String,
    val data: ByteArray
) : Comparable<Artifact> {
    private val comparableVersion = ComparableVersion(version)
    val hash = Hashing.getSha256(data)

    override fun compareTo(other: Artifact): Int {
        return comparableVersion.compareTo(other.comparableVersion)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artifact

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }
}