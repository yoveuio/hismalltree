package com.muxu.demo.io.echo

import com.hismalltree.core.annotation.Slf4j
import com.hismalltree.core.annotation.Slf4j.Companion.log
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress
import kotlin.system.exitProcess

@Slf4j
class EchoServer(private val port: Int) {

    fun start() {
        val serverHandler = EchoServerHandler()
        val group = NioEventLoopGroup()
        try {
            val bootstrap = ServerBootstrap()
            bootstrap.group(group)
                .channel(NioServerSocketChannel::class.java)
                .localAddress(InetSocketAddress(port))
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(serverHandler)
                    }
                })
            val future = bootstrap.bind().sync()
            future.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                log.error("Usage: ${Companion::class.simpleName} <port>")
                exitProcess(1)
            }
            val port = Integer.parseInt(args[0])
            EchoServer(port).start()
        }

    }

}
