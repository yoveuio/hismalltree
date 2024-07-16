package com.hismalltree.connector.base.serializtion

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.java.typeutils.ResultTypeQueryable
import org.apache.flink.util.Collector
import java.io.Serializable

interface IDeserializationSchema<E, T> : Serializable, ResultTypeQueryable<T> {

    fun open(context: DeserializationSchema.InitializationContext) {
        // Nothing to do here for the default implementation.
    }

    fun deserialize(record: E, out: Collector<T>)

}