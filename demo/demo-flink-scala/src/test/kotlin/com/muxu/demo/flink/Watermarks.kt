package com.muxu.demo.flink

import org.apache.flink.api.common.eventtime.WatermarkGenerator
import org.apache.flink.api.common.eventtime.WatermarkOutput
import org.apache.flink.api.java.tuple.Tuple3
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.junit.Test

@Suppress("unused")
class Watermarks {

    @Test
    fun watermarksUdfSourceFunction() {
        val input = listOf(Tuple3("a", 1L, 1), Tuple3("b", 1L, 1), Tuple3("c", 3L, 1))

        val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(Configuration())
        env.addSource(object : SourceFunction<Tuple3<String, Long, Int>> {
            override fun run(ctx: SourceFunction.SourceContext<Tuple3<String, Long, Int>>?) {
                input.forEach { value ->
                    ctx?.collectWithTimestamp(value, value.f1)
                    ctx?.emitWatermark(Watermark(value.f1 - 1))
                }
                ctx?.emitWatermark(Watermark.MAX_WATERMARK)
            }

            override fun cancel() {
                TODO("Not yet implemented")
            }

        })
        env.execute()
    }

    /**
     * 使用已经定义的外部数据源的情况
     */
    fun watermarksTimestampAssigner() {
        val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(Configuration())
        env.fromCollection(listOf(Tuple3("a", 1L, 1), Tuple3("b", 1L, 1), Tuple3("c", 3L, 1)))
            .assignTimestampsAndWatermarks{
                object : WatermarkGenerator<Tuple3<String, Long, Int>> {
                    override fun onEvent(
                        event: Tuple3<String, Long, Int>?,
                        eventTimestamp: Long,
                        output: WatermarkOutput?
                    ) {
                        output?.emitWatermark(event?.f1?.let { org.apache.flink.api.common.eventtime.Watermark(it) })
                    }

                    override fun onPeriodicEmit(output: WatermarkOutput?) {
                        // not needed
                    }
                }
            }
            .keyBy{t -> t.f0}
            .window(EventTimeSessionWindows.withGap(Time.seconds(10)))
            .sum(2)
            .print()
        env.execute()
    }

}