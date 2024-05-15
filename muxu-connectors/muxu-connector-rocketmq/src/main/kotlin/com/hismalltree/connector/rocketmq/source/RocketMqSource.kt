package com.hismalltree.connector.rocketmq.source

import com.hismalltree.connector.rocketmq.serialization.RocketMqDeserializationSchema
import com.hismalltree.connector.rocketmq.source.reader.*
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.connector.source.*
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.metrics.MetricGroup
import org.apache.flink.util.UserCodeClassLoader
import java.util.function.Supplier

/**
 *
 * @param <OUT> The type of records produced by the source.
 */
class RocketMqSource<OUT>(
    private val configuration: Configuration,
    private val boundedness: Boundedness,
    private val deserializationSchema: RocketMqDeserializationSchema<OUT>
) : Source<OUT, RocketMQSourceSplit, RocketMQSourceEnumState> {

    override fun createReader(readerContext: SourceReaderContext?): SourceReader<OUT, RocketMQSourceSplit> {
        deserializationSchema.open(
            object : DeserializationSchema.InitializationContext {
                override fun getMetricGroup(): MetricGroup {
                    return readerContext!!.metricGroup().addGroup("deserializer")
                }

                override fun getUserCodeClassLoader(): UserCodeClassLoader {
                    return readerContext!!.userCodeClassLoader
                }
            })

        val splitReaderSupplier = Supplier<SplitReader<MessageView, RocketMQSourceSplit>> {
            RocketMqSplitReader(
                configuration,
                readerContext!!
            )
        }
        val rocketmqSourceFetcherManager =
            RocketMqSourceFetcherManager(splitReaderSupplier, configuration)
        val recordEmitter = RocketMqRecordEmitter(deserializationSchema)

        return RocketMqSourceReader(
            rocketmqSourceFetcherManager,
            recordEmitter,
            configuration,
            readerContext!!,
        )
    }

    override fun getBoundedness(): Boundedness {
        return this.boundedness
    }

    override fun getSplitSerializer(): SimpleVersionedSerializer<RocketMQSourceSplit> {
        TODO("Not yet implemented")
    }

    override fun getEnumeratorCheckpointSerializer(): SimpleVersionedSerializer<RocketMQSourceEnumState> {
        TODO("Not yet implemented")
    }

    override fun restoreEnumerator(
        p0: SplitEnumeratorContext<RocketMQSourceSplit>?,
        p1: RocketMQSourceEnumState?
    ): SplitEnumerator<RocketMQSourceSplit, RocketMQSourceEnumState> {
        TODO("Not yet implemented")
    }

    override fun createEnumerator(p0: SplitEnumeratorContext<RocketMQSourceSplit>?): SplitEnumerator<RocketMQSourceSplit, RocketMQSourceEnumState> {
        TODO("Not yet implemented")
    }
}