package com.hismalltree.connector.rocketmq.serialization

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation

class RocketMQDeserializationSchemaWrapper<T>(
    private val deserializationSchema: DeserializationSchema<T>
) : RocketMqDeserializationSchema<T> {

    override fun getProducedType(): TypeInformation<T> {
        return deserializationSchema.producedType
    }
}