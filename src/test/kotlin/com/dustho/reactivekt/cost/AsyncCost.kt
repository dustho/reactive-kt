package com.dustho.reactivekt.cost

import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

fun main() {
    val count = AtomicLong()
    measureTimeMillis {
        Flux.range(1, 200).doOnNext {
            Flux.range(1, 100_000).doOnNext {
                count.incrementAndGet()
            }.subscribe()
        }.blockLast()
    }.let { println("count: $count, elapsed: ${it}ms") }
}