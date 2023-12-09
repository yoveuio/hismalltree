package com.muxu.demo.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DemoSpringApplication

fun main(args: Array<String>) {
    SpringApplication.run(DemoSpringApplication::class.java, *args)
}
