package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ShowProfile: PacketPayload(MinecraftPacketConstants.SHOW_PROFILE) {
    var xuid: String = ""

    override suspend fun encode() {
        writeString(xuid)
    }
}