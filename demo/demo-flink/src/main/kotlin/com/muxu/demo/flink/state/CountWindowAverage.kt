package com.muxu.demo.flink.state

import org.apache.flink.api.common.functions.OpenContext
import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.StateTtlConfig
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeHint
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.util.Collector
import java.time.Duration


class CountWindowAverage : RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>>() {

    /**
     * The ValueState handle. The first field is the count, the second field a running sum.
     */
    private var sum: ValueState<Tuple2<Long, Long>?>? = null

    override fun flatMap(input: Tuple2<Long, Long>, out: Collector<Tuple2<Long, Long>>) {

        // access the state value
        val currentSum: Tuple2<Long, Long> = sum?.value() ?: Tuple2.of(0L, 0L)


        // update the count
        currentSum.f0 += 1


        // add the second field of the input value
        currentSum.f1 += input.f1


        // update the state
        sum!!.update(currentSum)


        // if the count reaches 2, emit the average and clear the state
        if (currentSum.f0 >= 2) {
            out.collect(Tuple2(input.f0, currentSum.f1 / currentSum.f0))
            sum!!.clear()
        }
    }

    override fun open(openContext: OpenContext?) {
        val ttlConfig = StateTtlConfig.newBuilder(Duration.ofSeconds(1))
            .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
            .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
            .build()

        val stateDescriptor = ValueStateDescriptor(
            "average",  // the state name
            TypeInformation.of(object :
                TypeHint<Tuple2<Long, Long>>() {})
        )
        stateDescriptor.enableTimeToLive(ttlConfig)
        sum = runtimeContext.getState(stateDescriptor)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            // this can be used in a streaming program like this (assuming we have a StreamExecutionEnvironment env)
            env.fromData(
                Tuple2.of(1L, 3L),
                Tuple2.of(1L, 5L),
                Tuple2.of(1L, 7L),
                Tuple2.of(1L, 4L),
                Tuple2.of(1L, 2L)
            )
                .keyBy { value -> value.f0 }
                .flatMap(CountWindowAverage())
                .print()
            env.execute()
        }
    }

}