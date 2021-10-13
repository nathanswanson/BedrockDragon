package bedrockDragon.network.raknet.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import mu.KotlinLogging

class RaknetHTTPServerListener: ByteToMessageDecoder(), RakNetServerListener {

    val logger = KotlinLogging.logger {}

    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?, out: MutableList<Any>?) {
        logger.info {ctx}
    }
}