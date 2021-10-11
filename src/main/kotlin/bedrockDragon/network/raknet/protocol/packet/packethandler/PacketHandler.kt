package bedrockDragon.network.raknet.protocol.packet.packethandler

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.peer.RakNetPeer
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import mu.KotlinLogging
import java.net.InetSocketAddress

val logger = KotlinLogging.logger {}

abstract class PacketHandler(val channel : Channel) {

    var finished: Boolean = false
    open fun responseToClient() {}
    open fun responseToServer() {}

}

