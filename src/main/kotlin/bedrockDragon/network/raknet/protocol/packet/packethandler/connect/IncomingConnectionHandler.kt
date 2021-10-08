package bedrockDragon.network.raknet.protocol.packet.packethandler.connect

import bedrockDragon.network.raknet.protocol.packet.packethandler.PacketHandler
import bedrockDragon.network.raknet.protocol.packet.packethandler.logger
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class IncomingConnectionHandler(val sender: RakNetPeer, val packet: EncapsulatedPacket, channel : Channel) : PacketHandler(channel) {
    override fun responseToClient() {
        logger.info { "test" }
    }


}