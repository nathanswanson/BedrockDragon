package bedrockDragon.network.raknet.handler

import io.netty.channel.Channel
import mu.KotlinLogging


abstract class PacketHandler(val channel : Channel) {

    val logger = KotlinLogging.logger {}

    var finished: Boolean = false
    open fun responseToClient() {}
    open fun responseToServer() {}

}

