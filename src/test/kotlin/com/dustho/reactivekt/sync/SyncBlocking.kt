package com.dustho.reactivekt.sync

import com.dustho.reactivekt.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

private val logger: Logger = LoggerFactory.getLogger("SyncBlockingKt")

fun main() {
    measureTimeMillis {
        subA()
        subB()
    }.let { logger.info { ">> elapsed: $it ms" } }
}

private fun subA() {
    logger.info { "Start A" }
    Thread.sleep(1000)
    logger.info { "End A" }
}

private fun subB() {
    logger.info { "Start B" }
    Thread.sleep(1000)
    logger.info { "End B" }
}