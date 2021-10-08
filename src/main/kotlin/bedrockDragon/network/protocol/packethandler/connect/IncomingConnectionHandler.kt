package bedrockDragon.network.protocol.packethandler.connect

import bedrockDragon.network.protocol.packethandler.PacketHandler
import bedrockDragon.network.protocol.packethandler.logger
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class IncomingConnectionHandler(val sender: RakNetClientPeer, val packet: EncapsulatedPacket, channel : Channel) : PacketHandler(channel) {
    override fun responseToClient() {
        logger.info { "test" }
    }


}