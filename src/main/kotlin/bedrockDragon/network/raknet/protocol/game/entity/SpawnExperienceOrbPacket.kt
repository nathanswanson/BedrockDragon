package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class SpawnExperienceOrbPacket: PacketPayload(MinecraftPacketConstants.SPAWN_EXPERIENCE_ORB) {
    lateinit var position: Float3
    var count = 0 //sVarInt

    override fun encode() {
        writeVector3(position)
        writeVarInt(count)
    }
}