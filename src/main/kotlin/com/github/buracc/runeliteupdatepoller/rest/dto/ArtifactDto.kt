package com.github.buracc.runeliteupdatepoller.rest.dto

import java.net.URL

data class ArtifactDto(
    val fileName: String,
    val version: String,
    val hash: String,
    val url: URL
)