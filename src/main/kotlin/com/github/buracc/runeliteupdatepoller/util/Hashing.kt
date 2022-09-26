package com.github.buracc.runeliteupdatepoller.util

import java.security.MessageDigest

object Hashing {
    fun getSha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("Sha256")
        md.update(bytes)
        val digest = md.digest()
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }
}