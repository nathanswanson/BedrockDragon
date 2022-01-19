package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class MoveEntityAbsolute: PacketPayload(MinecraftPacketConstants.MOVE_ENTITY_ABSOLUTE) {
    var runtimeEntityId = -1L //vLong
    var flags = 0 //byte
    lateinit var positon: Float3

    override fun encode() {
        writeVarLong(runtimeEntityId)
        writeUnsignedByte(flags)
        writeVector3(positon)
    }
}