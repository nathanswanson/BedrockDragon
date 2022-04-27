package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

/**
 * Sets client time which is important to show day night cycle.
 * @author Nathan Swanson
 * @since ALPHA
 */
class SetTimePacket: PacketPayload(MinecraftPacketConstants.SET_TIME) {
    var time = 0
    override suspend fun encode() {
        writeVarInt(time)
    }
}