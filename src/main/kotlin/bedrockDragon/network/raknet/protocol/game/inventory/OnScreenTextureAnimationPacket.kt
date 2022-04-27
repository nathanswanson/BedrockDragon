package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class OnScreenTextureAnimationPacket: PacketPayload(MinecraftPacketConstants.ON_SCREEN_TEXTURE_ANIMATION) {
    var animationType = -1

    override suspend fun encode() {
        writeInt(animationType)
    }
}