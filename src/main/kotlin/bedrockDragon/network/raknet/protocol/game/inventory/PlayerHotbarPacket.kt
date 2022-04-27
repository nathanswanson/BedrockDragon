package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class PlayerHotbarPacket: PacketPayload(MinecraftPacketConstants.PLAYER_HOTBAR) {
    var selectedHotbarSlot = -1 //vInt
    var containerId = -1 //byte
    var selectHotbarSlot = false

    override suspend fun encode() {
        writeVarInt(selectedHotbarSlot)
        writeUnsignedByte(containerId)
        writeBoolean(selectHotbarSlot)
    }

    override fun decode(packet: Packet) {
        selectedHotbarSlot = packet.readVarInt()
        containerId = packet.readUnsignedByte().toInt()
        selectHotbarSlot = packet.readBoolean()
    }
}