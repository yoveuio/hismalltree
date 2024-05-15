package com.hismalltree.connector.rocketmq.source.consumer

import org.apache.flink.configuration.Configuration
import org.apache.rocketmq.common.message.MessageQueue
import java.util.concurrent.CompletableFuture

class RocketMqConsumerImpl(private val configuration: Configuration) : RocketMqConsumer {
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun getConsumerGroup(): String {
        TODO("Not yet implemented")
    }

    override fun fetchMessageQueue(topic: String): CompletableFuture<Collection<MessageQueue>> {
        TODO("Not yet implemented")
    }

    override fun assign(messageQueue: MessageQueue) {
        TODO("Not yet implemented")
    }

    override fun commitOffset(messageQueue: MessageQueue?, offset: Long): CompletableFuture<Void?>? {
        TODO("Not yet implemented")
    }
}