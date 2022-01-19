package bedrockDragon.network.raknet.protocol.game.sound

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class PlaySoundPacket: PacketPayload(MinecraftPacketConstants.PLAY_SOUND) {
    var soundName = ""
    lateinit var soundPosition: Float3
    var volume = -1f
    var pitch = -1f

    override fun encode() {
        writeString(soundName)
        writeVector3(soundPosition)
        writeFloat(volume)
        writeFloat(pitch)
    }
}