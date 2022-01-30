package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class RespawnPacket: PacketPayload(MinecraftPacketConstants.RESPAWN) {

    val SEARCHING_FOR_SPAWN = 0
    val SERVER_READY_SPAWN = 1
    val CLIENT_READY_SPAWN = 2

    lateinit var position: Float3
    var state = SEARCHING_FOR_SPAWN //byte
    var runtimeEntityId = -1L //vLong

    override fun encode() {
        writeVector3(position)
        writeByte(state)
        writeUnsignedVarLong(runtimeEntityId)
    }
}