package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class ContainerClosePacket: PacketPayload(MinecraftPacketConstants.CONTAINER_CLOSE) {
    var windowId = 0 //Byte
    var serverInitiated = false
    override suspend fun encode() {
        writeUnsignedByte(windowId)
        writeBoolean(serverInitiated)
    }

    override fun decode(packet: Packet) {
        windowId = packet.readUnsignedByte().toInt()
        serverInitiated = packet.readBoolean()
    }
}