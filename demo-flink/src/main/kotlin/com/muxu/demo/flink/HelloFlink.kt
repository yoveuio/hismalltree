package com.muxu.demo.flink

import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.execution.Environment

object HelloFlink {

    @JvmStatic
    fun main(args: Array<String>) {
        val env = ExecutionEnvironment.createLocalEnvironmentWithWebUI(Configuration())
        env.execute()
    }

}