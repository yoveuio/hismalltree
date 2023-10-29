package com.muxu.demo.flink

import com.muxu.demo.flink.entity.SensorReading
import com.muxu.demo.flink.source.SensorSource
import com.muxu.demo.flink.timeassinger.SensorTimeAssigner
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.windowing.WindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.junit.Test

class AverageSensorReadings {

    fun testCommonReadings() {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        env.config.setAutoWatermarkInterval(1000L)

        val sensorSource = env.addSource(SensorSource())
            .assignTimestampsAndWatermarks(SensorTimeAssigner())

        val avgTemp = sensorSource.map { r ->
            SensorReading(r.id, r.timestamp, r.temperature)
        }
            .keyBy { it.id }
            .timeWindow(Time.seconds(1))
            .apply(TemperatureAverage())

        avgTemp.print()
        env.execute("Compute average sensor temperature")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AverageSensorReadings().testCommonReadings()
        }
    }

}

class TemperatureAverage: WindowFunction<SensorReading, SensorReading, String, TimeWindow> {
    override fun apply(
        key: String,
        window: TimeWindow,
        input: MutableIterable<SensorReading>,
        out: Collector<SensorReading>
    ) {
        // compute the average temperature
        val (cnt, sum) = input.fold(Pair(0, 0.0)) { c, r -> Pair(c.first + 1, c.second + r.temperature) }
        val avgTemp = sum / cnt
        out.collect(SensorReading(key, window.end, avgTemp))
    }

}
