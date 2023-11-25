package com.muxu.demo.debezium

import org.apache.kafka.connect.runtime.WorkerConfig
import org.apache.kafka.connect.storage.OffsetBackingStore
import org.apache.kafka.connect.util.Callback
import java.nio.ByteBuffer
import java.util.concurrent.Future

class CustomOffsetBackingStore: OffsetBackingStore {
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun get(keys: MutableCollection<ByteBuffer>?): Future<MutableMap<ByteBuffer, ByteBuffer>> {
        TODO("Not yet implemented")
    }

    override fun set(values: MutableMap<ByteBuffer, ByteBuffer>?, callback: Callback<Void>?): Future<Void> {
        TODO("Not yet implemented")
    }

    override fun connectorPartitions(connectorName: String?): MutableSet<MutableMap<String, Any>> {
        TODO("Not yet implemented")
    }

    override fun configure(config: WorkerConfig?) {
        TODO("Not yet implemented")
    }
}