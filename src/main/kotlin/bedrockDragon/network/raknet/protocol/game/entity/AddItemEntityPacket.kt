package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class AddItemEntityPacket: PacketPayload(MinecraftPacketConstants.ADD_ITEM_ENTITY) {
    var entityIdSelf: Long = 0 //sSarLong
    var runtimeId: Long = 0 //uVarLong
    lateinit var item: Item
    lateinit var pos: Float3
    lateinit var velocity: Float3
    //metadata
    var isFromFishing = false

    override suspend fun encode() {
        writeVarLong(entityIdSelf)
        writeUnsignedVarLong(runtimeId)
        writeItem(item)
        writeVector3(pos)
        writeVector3(velocity)
        writeUnsignedVarInt(0)
        writeBoolean(isFromFishing)
    }
}