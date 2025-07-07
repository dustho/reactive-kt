package com.dustho.reactivekt.ch2

import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

class FluxOperatorTest {

    /**
     * - just: 0개 이상의 데이터로 시작
     * - empty: 빈 데이터로 시작 (Error 발생 시, 전파하는 용도)
     * - from..: 객체 데이터로 시작
     */
    @Test
    fun testFluxFromDate() {
        Flux.just(1, 2, 3).subscribe { println(it) }
        Flux.fromIterable(listOf(1, 2, 3)).subscribe { println(it) }
    }

    /**
     * - create: 동기 방식으로 처리되는 객체를 반환
     * - defer: Flux 객체를 반환
     */
    @Test
    fun testFluxFromFunction() {
        val subscribeOn = Flux.create {
            it.next(1)
            it.next(2)
            it.next(3)
            it.complete()
        }.subscribeOn(Schedulers.boundedElastic())
        subscribeOn.subscribe { println(it) }

        Flux.defer { Flux.just(listOf(1, 2, 3)) }.subscribe { println(it) }
    }

    @Test
    fun testSinkDetail1() {
        val flux = Flux.create {
            val counter = AtomicInteger(0)
            recursiveFunction1(it, counter)
        }.subscribeOn(Schedulers.boundedElastic())
        flux.subscribe { println(it) }
    }

    fun recursiveFunction1(sink: FluxSink<Int>, counter: AtomicInteger) {
        if (counter.get() < 10) {
            sink.next(counter.getAndIncrement())
            recursiveFunction1(sink, counter)
        } else {
            sink.complete()
        }
    }

    @Test
    fun testSinkDetail2() {
        val flux = Flux.create {
            recursiveFunction2(it)
        }
            .contextWrite(Context.of("counter", AtomicInteger(0)))
            .subscribeOn(Schedulers.boundedElastic())

        flux.subscribe { println(it) }
    }

    // Webflux 에서는 ThreadLocal 대신 context 를 사용해야 한다
    fun recursiveFunction2(sink: FluxSink<Int>) {
        val counter = sink.contextView().get<AtomicInteger>("counter")
        if (counter.get() < 10) {
            sink.next(counter.getAndIncrement())
            recursiveFunction2(sink)
        } else {
            sink.complete()
        }
    }


    @Test
    fun testFluxWithCollectList() {
        val collectListMono: Mono<List<Int>> = Flux.just(1, 2, 3)
            .map { it * 2 }
            .collectList();
        collectListMono.subscribe { println(it) }
    }
}