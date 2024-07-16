package com.hismalltree.connector.rocketmq.source

import org.apache.flink.api.connector.source.SourceSplit
import org.apache.rocketmq.common.message.MessageQueue

open class RocketMQSourceSplit(
    val topic: String,
    val brokerName: String,
    val queueId: Int,
    val startingOffset: Long,
    val stoppingOffset: Long
) : SourceSplit {

    fun getMessageQueue(): MessageQueue {
        return MessageQueue(topic, brokerName, queueId)
    }

    override fun splitId(): String {
        return topic + SEPARATOR + brokerName + SEPARATOR + queueId
    }

    companion object {
        private const val SEPARATOR: String = "#"
    }

}
