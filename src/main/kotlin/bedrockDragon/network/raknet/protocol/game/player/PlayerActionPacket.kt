package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class PlayerActionPacket: PacketPayload(MinecraftPacketConstants.PLAYER_ACTION) {

    companion object {
        //attribution: Nukkit had this list in java
        val ACTION_START_BREAK = 0
        val ACTION_ABORT_BREAK = 1
        val ACTION_STOP_BREAK = 2
        val ACTION_GET_UPDATED_BLOCK = 3
        val ACTION_DROP_ITEM = 4
        val ACTION_START_SLEEPING = 5
        val ACTION_STOP_SLEEPING = 6
        val ACTION_RESPAWN = 7
        val ACTION_JUMP = 8
        val ACTION_START_SPRINT = 9
        val ACTION_STOP_SPRINT = 10
        val ACTION_START_SNEAK = 11
        val ACTION_STOP_SNEAK = 12
        val ACTION_DIMENSION_CHANGE_REQUEST = 13 //sent when dying in different dimension
        val ACTION_DIMENSION_CHANGE_ACK = 14 //sent when spawning in a different dimension to tell the server we spawned
        val ACTION_START_GLIDE = 15
        val ACTION_STOP_GLIDE = 16
        val ACTION_BUILD_DENIED = 17
        val ACTION_CONTINUE_BREAK = 18
        val ACTION_SET_ENCHANTMENT_SEED = 20
        val ACTION_START_SWIMMING = 21
        val ACTION_STOP_SWIMMING = 22
        val ACTION_START_SPIN_ATTACK = 23
        val ACTION_STOP_SPIN_ATTACK = 24
    }


    var entityId = -1L //uVarLong
    var action = -1 //varInt
    lateinit var coord: Float3 //blockVector
    var face = -1 //sVarInt

    override fun decode(packet: Packet) {
        entityId = packet.readUnsignedVarLong()
        action = packet.readVarInt()
        coord = packet.readBlockCoordinates()
        face = packet.readVarInt()
    }

    override fun toString(): String {
        return """PlayerActionPacket:
            |   entityId: $entityId
            |   action: $action
            |   coord: $coord
            |   face: $face
        """.trimMargin()
    }
}