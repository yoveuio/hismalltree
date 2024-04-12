package com.muxu.demo.flink.state

import org.apache.flink.api.common.state.ListState
import org.apache.flink.api.common.state.ListStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeHint
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.runtime.state.FunctionInitializationContext
import org.apache.flink.runtime.state.FunctionSnapshotContext
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction

class BufferingSink(
    private val threshold: Int,
    private val bufferedElement: MutableList<Tuple2<String?, Int?>?> = ArrayList()
) : SinkFunction<Tuple2<String?, Int?>?>, CheckpointedFunction {

    @Transient
    private var checkpointState: ListState<Tuple2<String?, Int?>?>? = null

    override fun invoke(value: Tuple2<String?, Int?>?, context: SinkFunction.Context?) {
        bufferedElement.add(value ?: throw IllegalArgumentException("value cannot be null"))
        if (bufferedElement.size == threshold) {
            for (element in bufferedElement) {
                // write the buffered elements to an external system
                println("write $element to external system")
            }
            bufferedElement.clear()
        }
    }

    override fun snapshotState(context: FunctionSnapshotContext?) {
        checkpointState?.update(bufferedElement)
    }

    override fun initializeState(context: FunctionInitializationContext?) {
        val descriptor = ListStateDescriptor(
            "buffered-elements",
            TypeInformation.of(object : TypeHint<Tuple2<String?, Int?>?>() {})
        )
        checkpointState = context?.operatorStateStore?.getListState(descriptor)
        if (context?.isRestored == true) {
            for (element in checkpointState?.get() ?: emptyList()) {
                bufferedElement.add(element)
            }
        }
    }
}