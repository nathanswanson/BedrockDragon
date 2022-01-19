package bedrockDragon.network.raknet.protocol.game.block

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class LecturnUpdatePacket: PacketPayload(MinecraftPacketConstants.LECTERN_UPDATE) {
    var page = 0b00
    var totalPage = 0b00
    lateinit var blockPosition : Float3 //blockVector
    var droppingBook = false

    override fun decode(packet: Packet) {
        page = readUnsignedByte().toInt()
        totalPage = readUnsignedByte().toInt()
       // blockPosition = readBlockCoordinate()
        droppingBook = readBoolean()

    }
}