package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class MobEquipmentPacket: PacketPayload(MinecraftPacketConstants.MOB_EQUIPMENT) {
    var runtimeEnityId = -1L //vLong
    lateinit var item: Item
    var slot: Byte = 0
    var selectedSlot: Byte = 0
    var windowId: Byte = 0

    override fun encode() {
        writeVarLong(runtimeEnityId)
        writeItem(item)
        writeUnsignedByte(slot.toInt())
        writeUnsignedByte(selectedSlot.toInt())
        writeByte(windowId)
    }

    override fun decode(packet: Packet) {
        runtimeEnityId = packet.readVarLong()
        item = packet.readItem()
        slot = packet.readUnsignedByte().toByte()
        windowId = packet.readByte()
    }
}