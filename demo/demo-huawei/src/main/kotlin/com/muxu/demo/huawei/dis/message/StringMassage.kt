package com.muxu.demo.huawei.dis.message

import java.nio.ByteBuffer

data class StringMassage(
    val message: String
): DISMessage {

    override fun toBuffer(): ByteBuffer {
        return ByteBuffer.wrap(message.toByteArray())
    }

}
