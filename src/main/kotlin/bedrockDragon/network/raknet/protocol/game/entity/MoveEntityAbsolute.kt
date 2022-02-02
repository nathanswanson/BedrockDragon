package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class MoveEntityAbsolute: PacketPayload(MinecraftPacketConstants.MOVE_ENTITY_ABSOLUTE) {
    var runtimeEntityId = -1L //vLong
    var flags = 0 //byte
    lateinit var positon: Float3
    var rotation = Float3(0f,0f,0f)

    //flags
    var onGround: Boolean = false //0x01
    var teleport: Boolean = false //0x02
    var forceMove: Boolean = false //0x04
    override fun encode() {
        flags = if(onGround) 0x01 else 0
        flags = flags or if(teleport) 0x02 else 0
        flags = flags or if(forceMove) 0x04 else 0
        writeUnsignedVarLong(runtimeEntityId)
        writeByte(flags)
        writeVector3(positon)
        writeByte(rotation.x.toInt())
        writeByte(rotation.y.toInt())
        writeByte(rotation.z.toInt())
    }
}