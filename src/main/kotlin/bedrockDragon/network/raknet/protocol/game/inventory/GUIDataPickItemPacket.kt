package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class GUIDataPickItemPacket: PacketPayload(MinecraftPacketConstants.GUI_DATA_PICK_ITEM) {
    var itemName = ""
    var itemEffects = ""
    var hotbarSlot = -1 //wiki.vg says it does not work but I have not tested.

    override suspend fun encode() {
        writeString(itemName)
        writeString(itemEffects)
        writeInt(hotbarSlot)
    }
}