package bedrockDragon.network.raknet.handler.packethandler

import io.netty.channel.Channel
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

abstract class PacketHandler(val channel : Channel) {

    var finished: Boolean = false
    open fun responseToClient() {}
    open fun responseToServer() {}

}

