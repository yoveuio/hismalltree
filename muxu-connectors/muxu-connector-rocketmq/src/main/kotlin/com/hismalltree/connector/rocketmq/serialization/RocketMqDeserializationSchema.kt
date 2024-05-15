package com.hismalltree.connector.rocketmq.serialization

import com.hismalltree.connector.base.serializtion.IDeserializationSchema
import com.hismalltree.connector.rocketmq.source.reader.MessageView
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.util.Collector

interface RocketMqDeserializationSchema<T> : IDeserializationSchema<MessageView, T> {
    override fun deserialize(record: MessageView, out: Collector<T>) {
        // Nothing to do here for the default implementation.
    }
}