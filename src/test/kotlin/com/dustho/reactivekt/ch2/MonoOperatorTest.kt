package com.dustho.reactivekt.ch2

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.test.Test

class MonoOperatorTest {

    /**
     * - just: 데이터 1개로 시작
     * - empty: Error 발생 시, 전파하는 용도로 사용
     */
    @Test
    fun startMonoFromData() {
        Mono.just(1).subscribe{ println("data = $it") }
        Mono.empty<Unit>().subscribe{ println("data = $it") }
    }

    /**
     * - fromCallable: 동기 방식으로 처리되는 객체를 반환 (ex. restTemplate, Jpa 마이그레이션)
     * - defer: Mono 객체를 반환 -> Mono.just(...)의 수행을 subscribe() 호출 시점으로 미룰 수 있음 (ex. N개의 작업을 하나의 Mono 로 제어할 때)
     */
    @Test
    fun startMonoFromFunction() {
        val monoFromCallable: Mono<String> = Mono.fromCallable { "Hello, fromCallable!" }
            .subscribeOn(Schedulers.boundedElastic())
        monoFromCallable.subscribe { println(it) }

        val monoDefer: Mono<String> = Mono.defer { Mono.just("Hello, defer!") }
            .subscribeOn(Schedulers.boundedElastic())
        monoDefer.subscribe { println(it) }
    }

    @Test
    fun monoToFlux() {
        val flux = Mono.just(1).flatMapMany { Flux.just(it, it + 1, it + 2, it + 3) }
        flux.subscribe { println("data = $it") }
    }
}