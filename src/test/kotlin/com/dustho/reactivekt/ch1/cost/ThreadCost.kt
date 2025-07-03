package com.dustho.reactivekt.ch1.cost

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val latch = CountDownLatch(200)
    val count = AtomicLong()
    measureTimeMillis {
        repeat(200) {
            thread {
                repeat(100_000) {
                    count.incrementAndGet()
                }
                latch.countDown()
            }
        }
        latch.await()
    }.let { println("count: $count, elapsed: ${it}ms") }
}