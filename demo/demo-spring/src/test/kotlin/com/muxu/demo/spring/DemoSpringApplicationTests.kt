package com.muxu.demo.spring

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit.jupiter.SpringExtension

@Profile("test")
@ExtendWith(value = [SpringExtension::class])
@SpringBootTest
class DemoSpringApplicationTests {

}
