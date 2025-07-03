package com.dustho.reactivekt.ch1.sync

import com.dustho.reactivekt.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

private val logger: Logger = LoggerFactory.getLogger("SyncNonBlockingKt")

fun main() {
    measureTimeMillis {
        val future = subA()
        subB()
        while (!future.isDone) {
            logger.info { "... waiting A" }
            Thread.sleep(200)
        }
    }.let { logger.info { ">> elapsed: $it ms" } }
}

private fun subA(): CompletableFuture<Unit> {
    return CompletableFuture.supplyAsync {
        logger.info { "Start A" }
        Thread.sleep(3000)
        logger.info { "End A" }
    }
}

private fun subB() {
    logger.info { "Start B" }
    Thread.sleep(1000)
    logger.info { "End B" }
}

