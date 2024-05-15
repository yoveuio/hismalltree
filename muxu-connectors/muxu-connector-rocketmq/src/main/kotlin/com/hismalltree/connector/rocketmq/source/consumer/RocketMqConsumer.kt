package com.hismalltree.connector.rocketmq.source.consumer

import org.apache.rocketmq.common.message.MessageQueue
import java.util.concurrent.CompletableFuture

interface RocketMqConsumer {

    fun start()

    fun getConsumerGroup(): String

    fun fetchMessageQueue(topic: String): CompletableFuture<Collection<MessageQueue>>

    fun assign(messageQueue: MessageQueue)

    /**
     * Seek consumer group previously committed offset
     *
     * @param messageQueue rocketmq queue to locate single queue
     * @return offset for message queue
     */
    fun commitOffset(messageQueue: MessageQueue?, offset: Long): CompletableFuture<Void?>?

}