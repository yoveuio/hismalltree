package com.muxu.demo.io.echo

import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.CharsetUtil

@Slf4j
class EchoClientHandler: SimpleChannelInboundHandler<ByteBuf>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8))
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        log.info("Client received: ${msg.toString(CharsetUtil.UTF_8)}")
    }

}