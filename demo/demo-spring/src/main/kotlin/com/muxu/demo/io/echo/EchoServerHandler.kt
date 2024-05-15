package com.muxu.demo.io.echo

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil

@Sharable
class EchoServerHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val input = msg as ByteBuf
        println("Server received: ${input.toString(CharsetUtil.UTF_8)}")
        ctx.write(input)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        ctx?.writeAndFlush(Unpooled.EMPTY_BUFFER)
            ?.addListeners(ChannelFutureListener.CLOSE)
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace()
        ctx?.close()
    }

}