package com.dustho.reactivekt.ch2

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.test.Test

/**
 * flatMap: 중첩된 비동기 객체를 다음과 같이 평탄화해주는 역할
 * - Mono<Mono<T>> -> Mono<T>
 * - Flux<Mono<T>> -> Flux<T>
 * - Mono<Flux<T>> -> Flux<T>
 */
class OperatorFlatMapTest {

    @Test
    fun testOperatorFlatmap() {
        Flux.just(
            callWebClient("just 1단계 - 읽기", 1500),
            callWebClient("just 2단계 - 이해하기", 1000),
            callWebClient("just 3단계 - 설명하기", 500)
        ).flatMap { it }
            .subscribe { println("response = ${it}") }

        // flatMap 에서 데이터 처리 없이 그냥 반환하는 경우, merge 사용
        Flux.merge(
            callWebClient("merge 1단계 - 읽기", 1500),
            callWebClient("merge 2단계 - 이해하기", 1000),
            callWebClient("merge 3단계 - 설명하기", 500)
        ).subscribe { println("response = ${it}") }

        Flux.create {
            it.next(callWebClient("sink 1단계 - 읽기", 1500))
            it.next(callWebClient("sink 2단계 - 이해하기", 1000))
            it.next(callWebClient("sink 3단계 - 설명하기", 500))
        }.flatMap { it }
            .subscribe { println("response = ${it}") }

        Thread.sleep(2000)
    }

    // 호출 순서대로 반환을 보장하고 싶을 때는 flatMapSequential 을 사용 (단, 호출은 동일하게 비동기적으로 수행)
    // 호출까지 동기적으로 수행하고 싶다면 concat 을 사용
    @Test
    fun testOperatorFlatmapSequential() {
        Flux.just(
            callWebClient("just 1단계 - 읽기", 1500),
            callWebClient("just 2단계 - 이해하기", 1000),
            callWebClient("just 3단계 - 설명하기", 500)
        ).flatMapSequential { it }
            .subscribe { println("response = ${it}") }

        Flux.mergeSequential(
            callWebClient("just 1단계 - 읽기", 1500),
            callWebClient("just 2단계 - 이해하기", 1000),
            callWebClient("just 3단계 - 설명하기", 500)
        ).subscribe { println("response = ${it}") }

        Thread.sleep(2000)
    }

    fun callWebClient(request: String, delay: Long): Mono<String> {
        return Mono.defer {
            Thread.sleep(delay)
            Mono.just("${request} -> 딜레이 ${delay}s")
        }.subscribeOn(Schedulers.boundedElastic())
    }
}