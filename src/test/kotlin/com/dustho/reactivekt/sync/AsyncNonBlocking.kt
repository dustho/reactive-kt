package com.dustho.reactivekt.sync

import com.dustho.reactivekt.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import kotlin.system.measureTimeMillis

private val logger: Logger = LoggerFactory.getLogger("SyncNonBlockingKt")
private val singleThread = Schedulers.newSingle("worker")

fun main() {
    measureTimeMillis {
        Mono.zip(
            subA(),
            subB()
        ).subscribeOn(singleThread).block()
    }.let { logger.info { ">> elapsed: $it ms" } }
}

private fun subA(): Mono<Unit> {
    return Mono.fromCallable { logger.info { "Start A" } }
        .delayElement(Duration.ofMillis(1000))
        .publishOn(singleThread)
        .doOnNext { logger.info { "End A" } }
}

private fun subB(): Mono<Unit> {
    return Mono.fromCallable { logger.info { "Start B" } }
        .delayElement(Duration.ofMillis(1000))
        .publishOn(singleThread)
        .doOnNext { logger.info { "End B" } }
}

