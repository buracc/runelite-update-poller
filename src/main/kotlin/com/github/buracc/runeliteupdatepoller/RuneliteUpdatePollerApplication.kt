package com.github.buracc.runeliteupdatepoller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RuneliteUpdatePollerApplication

fun main(args: Array<String>) {
    runApplication<RuneliteUpdatePollerApplication>(*args)
}
