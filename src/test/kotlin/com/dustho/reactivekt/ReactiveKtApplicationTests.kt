package com.dustho.reactivekt

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ReactiveKtApplicationTests {

    @Test
    fun contextLoads() {
    }

}

inline fun Logger.info(message: () -> String) {
    if (isInfoEnabled) {
        info(message())
    }
}