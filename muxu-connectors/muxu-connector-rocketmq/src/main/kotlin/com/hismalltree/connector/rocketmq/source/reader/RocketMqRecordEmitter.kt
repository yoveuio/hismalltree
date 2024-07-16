package com.hismalltree.connector.rocketmq.source.reader

import com.hismalltree.connector.rocketmq.serialization.RocketMqDeserializationSchema
import com.hismalltree.connector.rocketmq.source.split.RocketMQSourceSplitState
import org.apache.flink.api.connector.source.SourceOutput
import org.apache.flink.connector.base.source.reader.RecordEmitter
import org.apache.flink.util.Collector
import java.io.IOException

class RocketMqRecordEmitter<T>(
    private val deserializationSchema: RocketMqDeserializationSchema<T>
) : RecordEmitter<MessageView, T, RocketMQSourceSplitState> {

    private val sourceOutputWrapper: SourceOutputWrapper<T> = SourceOutputWrapper()

    override fun emitRecord(
        element: MessageView, output: SourceOutput<T>, splitState: RocketMQSourceSplitState
    ) {
        try {
            sourceOutputWrapper.setSourceOutput(output)
            sourceOutputWrapper.setTimestamp(element.eventTime)
            deserializationSchema.deserialize(element, sourceOutputWrapper)
            splitState.currentOffset = element.queueOffset + 1
        } catch (e: Exception) {
            throw IOException("Failed to deserialize message due to", e)
        }
    }


}

private class SourceOutputWrapper<T> : Collector<T> {

    private var sourceOutput: SourceOutput<T>? = null

    private var timestamp: Long = 0

    override fun collect(record: T) {
        sourceOutput!!.collect(record, timestamp)
    }

    override fun close() {
        // do nothing
    }

    fun setSourceOutput(sourceOutput: SourceOutput<T>) {
        this.sourceOutput = sourceOutput
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
    }
}