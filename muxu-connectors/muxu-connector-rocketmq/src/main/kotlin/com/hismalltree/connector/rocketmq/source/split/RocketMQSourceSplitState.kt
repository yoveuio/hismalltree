package com.hismalltree.connector.rocketmq.source.split

import com.hismalltree.connector.rocketmq.source.RocketMQSourceSplit
import org.apache.rocketmq.remoting.protocol.header.ExtraInfoUtil.getBrokerName
import org.apache.rocketmq.remoting.protocol.header.ExtraInfoUtil.getQueueId

class RocketMQSourceSplitState(
    private val partitionSplit: RocketMQSourceSplit
): RocketMQSourceSplit(
    partitionSplit.topic,
    partitionSplit.brokerName,
    partitionSplit.queueId,
    partitionSplit.startingOffset,
    partitionSplit.stoppingOffset
) {
    var currentOffset: Long? = null

    /**
     * Use the current offset as the starting offset to create a new RocketMQSourceSplit.
     *
     * @return a new RocketMQSourceSplit which uses the current offset as its starting offset.
     */
    fun getSourceSplit(): RocketMQSourceSplit {
        return RocketMQSourceSplit(topic, brokerName, queueId, currentOffset!!, stoppingOffset)
    }
}