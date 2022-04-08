package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3


/*

Field	    Bit
Neighbors	0x01
Network	    0x02
No Graphic	0x04
Priority	0x08

 */
class UpdateBlockPacket: PacketPayload(MinecraftPacketConstants.UPDATE_BLOCK) {
    lateinit var coordinates: Float3 //BlockCoord
    var blockRuntimeId: Int = -1 //VarInt
    var flags: Int = 2 //VarInt
    var layer: Int = 0 //VarInt

    override fun encode() {
        writeBlockCoordinates(coordinates)
        writeUnsignedVarInt(blockRuntimeId)
        writeUnsignedVarInt(flags)
        writeUnsignedVarInt(layer)
    }
}