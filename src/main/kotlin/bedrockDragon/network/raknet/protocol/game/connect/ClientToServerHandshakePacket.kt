package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ClientToServerHandshakePacket: PacketPayload(MinecraftPacketConstants.CLIENT_TO_SERVER_HANDSHAKE) {

    override fun decode(packet: Packet) {
        super.decode(packet)
    }
}