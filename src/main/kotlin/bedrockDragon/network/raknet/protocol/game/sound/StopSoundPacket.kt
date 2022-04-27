package bedrockDragon.network.raknet.protocol.game.sound

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class StopSoundPacket: PacketPayload(MinecraftPacketConstants.STOP_SOUND) {
    var soundName = ""
    var stoppingAllSound = false

    override suspend fun encode() {
        writeString(soundName)
        writeBoolean(stoppingAllSound)
    }
}