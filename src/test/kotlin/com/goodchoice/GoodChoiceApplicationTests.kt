package com.goodchoice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local", "test")
class GoodChoiceApplicationTests {

    @Test
    fun contextLoads() {
    }
}
