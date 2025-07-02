package com.dustho.reactivekt.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

@RestController
@RequestMapping("/reactive")
class ReactiveExampleController {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 1 ~ 9까지 출력하는 API
     */
    @GetMapping("/one-nine/list")
    fun produceOnToNine(): List<Int> {
        val sink = mutableListOf<Int>()
        for (i in 1 .. 9) {
            logger.info("add number: $i")
            Thread.sleep(500)
            sink.add(i)
        }
        return sink
    }

    /**
     * 1 ~ 9까지 출력하는 API (Async + Blocking)
     */
    @GetMapping("/one-nine/flux")
    fun produceOnToNineFlux(): Flux<Int> {
        // logger.info("Flux 외부에서 사용중인 스레드: ${Thread.currentThread().name} ")
        return Flux.create<Int> {
            for (i in 1 .. 9) {
                logger.info("Flux 내부에서 사용중인 스레드: ${Thread.currentThread().name} ")
                Thread.sleep(500) // Blocking 유발
                it.next(i)
            }
            it.complete()
        }.subscribeOn(Schedulers.boundedElastic())
    }


}