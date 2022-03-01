package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class AddEntityPacket: PacketPayload(MinecraftPacketConstants.ADD_ENTITY) {
    var entitySelfId = -1L //sVarLong
    var runtimeEntityId = -1L //varLong
    var entityType = ""
    lateinit var position: Float3
    var velocity: Float3 = Float3(0f,0f,0f)
    var rotation: Float3 = Float3(0f,0f,0f)
    //attribute
    //metadata
    //links
    override fun encode() {
        writeVarLong(entitySelfId)
        writeUnsignedVarLong(runtimeEntityId)
        writeString(entityType)
        writeVector3(position)
        writeVector3(velocity)
        writeVector3(rotation)
        //attribute
        writeUnsignedVarInt(0)
        //meta
        writeUnsignedVarInt(0)
        //links
        writeUnsignedVarInt(0)
    }

}