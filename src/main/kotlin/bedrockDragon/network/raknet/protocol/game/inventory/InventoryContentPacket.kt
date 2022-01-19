package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class InventoryContentPacket: PacketPayload(MinecraftPacketConstants.INVENTORY_CONTENT) {
    var inventoryId = -1 //vInt
    var size = 0
    lateinit var itemStacks: Array<Item?>

    override fun encode() {
        writeUnsignedVarInt(inventoryId)
        writeUnsignedVarInt(size)
        itemStacks.forEach { writeItem(it) }
    }

    override fun decode(packet: Packet) {
        inventoryId = packet.readVarInt()
        //TODO()
    }
}