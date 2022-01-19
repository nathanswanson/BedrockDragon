package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class ContainerOpenPacket: PacketPayload(MinecraftPacketConstants.CONTAINER_OPEN) {
    var windowId = -1
    var type = -1
    lateinit var position: Float3 //blockCoord
    var entityId = -1L //uvarLong

    override fun encode() {
        writeByte(windowId)
        writeByte(type)
        writeBlockCoordinates(position)
        writeUnsignedVarLong(entityId)
    }
}