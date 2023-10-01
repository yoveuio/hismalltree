package com.muxu.demo.flink

import java.util.function.Function

/**
 * @author fangpeiyu.py
 */
object Hello {
    @JvmStatic
    fun main(args: Array<String>) {
        object : Function<Any?, Any?> {
            override fun apply(o: Any?): Any? {
                return null
            }

            override fun <V> compose(before: Function<in V, *>): Function<V, Any?> {
                return super.compose(before)
            }

            override fun <V> andThen(after: Function<in Any?, out V>): Function<Any?, V> {
                return super.andThen(after)
            }
        }
    }
}
