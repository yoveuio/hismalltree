package com.muxu.demo.spring

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit4.SpringRunner

@Profile("test")
@RunWith(SpringRunner::class)
@SpringBootTest
class BaseSpringTestService {
}