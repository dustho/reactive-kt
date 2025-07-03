package com.dustho.reactivekt.ch1.stream

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import kotlin.test.Test

@SpringBootTest
class FluxTest(
    @Autowired private val webClientBuilder: WebClient.Builder
) {

    private val webClient = webClientBuilder.baseUrl("http://localhost:8080").build()

    @Test
    fun produceOneToNineFlux() {
        val intFlux: Flux<Int> = Flux.create {
            for (i in 1 .. 9) {
                it.next(i)
            }
            it.complete()
        }
        intFlux.subscribe { println(it) }
    }

    @Test
    fun produceOneToNineFluxLikeStreamOperator() {
        Flux.fromIterable((1..9).toList())
            .map { it * 2 }
            .filter { it % 2 == 0 }
            .subscribe { println(it) }
    }

    @Test
    fun produceOneToNineFluxWithBlock() {
        val intFlux: Flux<Int> = Flux.create {
            for (i in 1 .. 9) {
                Thread.sleep(500)
                it.next(i)
            }
            it.complete()
        }
        intFlux.subscribe {
            println("Flux 내부에서 사용중인 스레드: ${Thread.currentThread().name}, 숫자: $it")
        }
        println("Flux 외부에서 사용중인 스레드: ${Thread.currentThread().name}")
    }

    @Test
    fun produceOneToNineFluxWithNonBlock() {
        val intFlux: Flux<Int> = Flux.create<Int?> {
            for (i in 1 .. 9) {
                Thread.sleep(500)
                it.next(i)
            }
            it.complete()
        }.subscribeOn(Schedulers.boundedElastic())
        intFlux.subscribe {
            println("Flux 내부에서 사용중인 스레드: ${Thread.currentThread().name}, 숫자: $it")
        }
        println("Flux 외부에서 사용중인 스레드: ${Thread.currentThread().name}")
        Thread.sleep(5000)
    }

    @Test
    fun produceOneToNineFluxWithWebclient() {
        val intFlux: Flux<Int> = webClient.get().uri("/reactive/one-nine/flux").retrieve().bodyToFlux<Int>()
        intFlux.subscribe {
            println("Flux 내부에서 사용중인 스레드: ${Thread.currentThread().name}, 숫자: $it")
        }
        println("Flux 외부에서 사용중인 스레드: ${Thread.currentThread().name}")
        Thread.sleep(5000)
    }


}