package com.muxu.demo.spring

import java.util.*

class Test {
}

fun main() {
    var fromString = UUID.fromString("hello")
    Thread.sleep(1000)
    var fromString2 = UUID.fromString("hello")
    println(fromString == fromString2)
}
