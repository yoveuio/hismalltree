package com.hismalltree.connector.rocketmq.source

import org.apache.rocketmq.common.message.MessageQueue

data class RocketMQSourceEnumState(
    val currentSplitAssignment: Set<MessageQueue>
)
