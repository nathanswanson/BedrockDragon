package bedrockDragon.debug

import bedrockDragon.DragonServer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import java.net.InetSocketAddress

val serverAddress = InetSocketAddress(19132)

class Client {
    val bootstrap = Bootstrap()
    val group = NioEventLoopGroup()
    val handler = ClientHandler()
    lateinit var channel: Channel
    fun connect() {
        bootstrap.channel(NioDatagramChannel::class.java).group(group).handler(handler)
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, false)
        channel = bootstrap.bind(serverAddress).sync().channel()
        
    }
}

fun main(args: Array<String>) {
    Client()
}