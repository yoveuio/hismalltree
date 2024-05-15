package com.hismalltree.connector.rocketmq.source.reader

import com.hismalltree.connector.rocketmq.source.RocketMQSourceSplit
import com.hismalltree.connector.rocketmq.source.consumer.RocketMqConsumer
import com.hismalltree.connector.rocketmq.source.consumer.RocketMqConsumerImpl
import org.apache.flink.api.connector.source.SourceReaderContext
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange
import org.apache.rocketmq.common.message.MessageQueue

class RocketMqSplitReader(
    configuration: Configuration,
    readerContext: SourceReaderContext,
    private val consumer: RocketMqConsumer = RocketMqConsumerImpl(configuration)
) : SplitReader<MessageView, RocketMQSourceSplit> {
    override fun close() {
        TODO("Not yet implemented")
    }

    override fun fetch(): RecordsWithSplitIds<MessageView> {
        TODO("Not yet implemented")
    }

    override fun wakeUp() {
        TODO("Not yet implemented")
    }

    override fun handleSplitsChanges(splitsChanges: SplitsChange<RocketMQSourceSplit>?) {
        TODO("Not yet implemented")
    }

    fun notifyCheckpointComplete(offsetsToCommit: Map<MessageQueue, Long>) {
        for ((key, value) in offsetsToCommit) {
            consumer.commitOffset(key, value)
        }
    }
}