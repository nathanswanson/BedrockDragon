package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class MovePlayerPacket: PacketPayload(MinecraftPacketConstants.MOVE_PLAYER) {
    var runtimeEntityId = -1L //varLong
    lateinit var position: Float3
    lateinit var rotation: Float3 //pitch | yaw | head yaw
    var mode: Byte = -1
    var onGround = false
    var ridingRuntimeEntityId = -1L
    var teleportationCause = -1L
    var entityType: Byte = -1

    override fun encode() {
        writeVarLong(runtimeEntityId)
        writeVector3(position)
        writeVector3(rotation)
        write(mode)
        writeBoolean(onGround)
        //writeFloat(ridingRuntimeEntityId)
        //writeVarLong(teleportationCause)
        //write(entityType)
    }

    override fun decode(packet: Packet) {
        runtimeEntityId = packet.readUnsignedVarLong()
        position = packet.readVector3()
        rotation = packet.readVector3()
        mode = packet.readByte()
        onGround = packet.readBoolean()
        //ridingRuntimeEntityId = readUnsignedVarLong()
       // teleportationCause = readVarLong()
        //entityType = readUnsignedByte().toByte()
    }

}