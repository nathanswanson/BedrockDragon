package bedrockDragon.network.raknet.protocol.game.resourcepacket

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ClientCacheStatusPacket: PacketPayload(MinecraftPacketConstants.CLIENT_CACHE_STATUS) {
    var supported: Boolean = false

    override fun decode(packet: Packet) {
        supported = packet.readBoolean()
    }

    override fun encode() {
        writeBoolean(supported)
    }
}