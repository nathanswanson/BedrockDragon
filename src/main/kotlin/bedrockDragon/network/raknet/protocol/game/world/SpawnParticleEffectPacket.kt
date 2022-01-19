package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class SpawnParticleEffectPacket: PacketPayload(MinecraftPacketConstants.SPAWN_PARTICLE_EFFECT) {
    var dimensionId: Byte = 0
    var uniqueEntityId = -1L //sVarLong
    lateinit var position: Float3
    var identifier = ""

    override fun encode() {
        writeByte(dimensionId)
        writeVarLong(uniqueEntityId)
        writeVector3(position)
        writeString(identifier)
    }
}