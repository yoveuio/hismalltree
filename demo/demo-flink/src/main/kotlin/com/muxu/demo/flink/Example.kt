package com.muxu.demo.flink

import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment


object Example {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        val flintstones: DataStream<Person> = env.fromElements(
            Person("Fred", 35),
            Person("Wilma", 35),
            Person("Pebbles", 2)
        )
        val adults: DataStream<Person> = flintstones.filter { person -> person.age!! >= 18 }
        adults.print()
        env.execute()
    }

    class Person {
        private var name: String? = null
        var age: Int? = null

        constructor()
        constructor(name: String?, age: Int?) {
            this.name = name
            this.age = age
        }

        override fun toString(): String {
            return name.toString() + ": age " + age.toString()
        }
    }
}