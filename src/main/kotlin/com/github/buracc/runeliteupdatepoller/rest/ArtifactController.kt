package com.github.buracc.runeliteupdatepoller.rest

import com.github.buracc.runeliteupdatepoller.repository.ArtifactRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/artifacts")
class ArtifactController(
    private val artifactRepository: ArtifactRepository
) {
    @GetMapping(produces = ["application/json"])
    fun getAll() = artifactRepository.artifacts.values.map { it.toDto() }

    @GetMapping("/latest", produces = ["application/json"])
    fun getLatest(@RequestParam(required = false) snapshot: Boolean?) = ResponseEntity.of(
        Optional.ofNullable(
            artifactRepository.getLatest(snapshot ?: false).run {
                this?.toDto()
            }
        )
    )

    @GetMapping("/latest/download", produces = ["application/octet-stream"])
    fun getLatestFile(
        @RequestParam(required = false) snapshot: Boolean?,
        response: HttpServletResponse
    ) = artifactRepository.getLatest(snapshot ?: false)
        ?.run {
            ResponseEntity.of(Optional.ofNullable(this))
                .headers.setContentDispositionFormData("attachment", fileName)
        }
}