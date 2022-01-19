package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class NetworkSettingsPacket: PacketPayload(MinecraftPacketConstants.NETWORK_SETTINGS) {
    var compressionThreshold = 500 //short

    override fun encode() {
        writeShort(compressionThreshold)
    }
}