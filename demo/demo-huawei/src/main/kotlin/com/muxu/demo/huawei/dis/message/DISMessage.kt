package com.muxu.demo.huawei.dis.message

import java.nio.ByteBuffer
import java.util.concurrent.ThreadLocalRandom

interface DISMessage {

    fun getPartitionKey(): String {
        return ThreadLocalRandom.current().nextInt(1000000).toString()
    }

    fun toBuffer(): ByteBuffer

}