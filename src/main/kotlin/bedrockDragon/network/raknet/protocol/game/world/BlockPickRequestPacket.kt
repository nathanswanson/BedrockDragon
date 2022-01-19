package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class BlockPickRequestPacket: PacketPayload(MinecraftPacketConstants.BLOCK_PICK_REQUEST) {
    lateinit var position: Float3
    var addUserData: Boolean = false
    var hotBarSlot: Byte = 0

    override fun decode(packet: Packet) {
        position = Float3(packet.readVarInt().toFloat(), packet.readVarInt().toFloat(), packet.readVarInt().toFloat())
        addUserData = packet.readBoolean()
        hotBarSlot = packet.readByte()
    }
}