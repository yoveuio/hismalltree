package com.hismalltree.connector.rocketmq.util

import com.hismalltree.connector.rocketmq.source.RocketMQSourceSplit
import org.apache.rocketmq.common.message.MessageQueue

object RocketMqUtils {

    const val SEPARATOR = "#"

    fun getMessageQueue(split: RocketMQSourceSplit): MessageQueue {
        return MessageQueue(split.topic, split.brokerName, split.queueId)
    }

}