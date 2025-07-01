package com.dustho.reactivekt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveKtApplication

fun main(args: Array<String>) {
    runApplication<ReactiveKtApplication>(*args)
}
