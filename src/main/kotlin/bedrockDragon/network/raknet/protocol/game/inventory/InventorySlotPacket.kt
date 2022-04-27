package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class InventorySlotPacket: PacketPayload(MinecraftPacketConstants.INVENTORY_SLOT) {
    var inventoryId = -1 //vInt
    var slot = -1 //vInt
    lateinit var item: Item

    override suspend fun encode() {
        writeUnsignedVarInt(inventoryId)
        writeUnsignedVarInt(slot)
        writeItem(item)
    }

    override fun decode(packet: Packet) {
        inventoryId = packet.readUnsignedVarInt().toInt()
        slot = packet.readUnsignedVarInt().toInt()
        item = packet.readItem()
    }
}