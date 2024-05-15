package com.muxu.demo.io.echo

import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress

@Slf4j
class EchoClient(
    private val host: String,
    private val port: Int
) {
    fun start() {
        val group = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
            bootstrap.group(group)
                .channel(NioSocketChannel::class.java)
                .remoteAddress(InetSocketAddress(host, port))
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(EchoClientHandler())
                    }
                })
            val future = bootstrap.connect().sync()
            future.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully().sync()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 2) {
                log.error("Usage: ${EchoClient::class.simpleName} <host> <port>")
                return
            }
            val host = args[0]
            val post = Integer.parseInt(args[1])
            EchoClient(host, post).start()
        }
    }
}