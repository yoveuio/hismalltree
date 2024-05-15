package com.hismalltree.streaming.etl.job

import com.hismalltree.streaming.etl.constant.Constants.ARG_OWNER
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

abstract class Job {

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()

    private var owner: String? = null

    fun configure(args: Array<String>) {
        val params = ParameterTool.fromArgs(args)
        this.owner = params[ARG_OWNER, ""]
    }

}
