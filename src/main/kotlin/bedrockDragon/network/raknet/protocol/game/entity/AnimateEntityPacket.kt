package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AnimateEntityPacket: PacketPayload(MinecraftPacketConstants.ANIMATE_ENTITY) {
    var animation = ""
    var nextState = ""
    var stopExpression = ""
    var controller = ""
    var blendOutTime = 0f //floatLE
    var entityArraySize = -1 //uVarInt
    lateinit var runtimeEntityId : Array<Long> //uVarLong[]

    override fun decode(packet: Packet) {
        packet.readString()
        packet.readString()
        packet.readString()
        packet.readString()
        packet.readFloatLE()
        packet.readUnsignedVarInt()
        for(i in 1..packet.readUnsignedVarInt()) {
            packet.readUnsignedVarLong()
        }
    }
}