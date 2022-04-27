package bedrockDragon.network.raknet.protocol.game.event

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class LevelSoundEventPacket: PacketPayload(MinecraftPacketConstants.LEVEL_SOUND_EVENT_THREE) {
    var soundId: Byte = 0
    lateinit var position: Float3
    var blockId = -1 //sVarInt
    var entityType = -1 //sVarInt
    var isBabyMob: Boolean = false
    var isGlobal: Boolean = false


    override suspend fun encode() {
        writeByte(soundId)
        writeVector3(position)
        writeVarInt(blockId)
        writeVarInt(entityType)
        writeBoolean(isBabyMob)
        writeBoolean(isGlobal)
    }
}