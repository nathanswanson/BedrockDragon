package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.*
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class EntityDataPacket: PacketPayload(MinecraftPacketConstants.SET_ENTITY_DATA) {
    var runtimeEntityId = 0L
    lateinit var metaTag: MetaTag
    var tick = 0L

    override fun encode() {
        writeUnsignedVarLong(runtimeEntityId)
        writeMetaData(metaTag)
        writeUnsignedVarLong(tick)
    }

    private fun writeMetaData(metaTag: MetaTag) {
        writeUnsignedVarInt(metaTag.size())
        metaTag.data.forEach {
            writeUnsignedVarInt(it.key)//id
            writeUnsignedVarInt(it.value.type)
            when(it.value.type) {
                DATA_TYPE_BYTE -> write(it.value.data as Byte)
                DATA_TYPE_INT -> writeInt(it.value.data as Int)
                DATA_TYPE_SHORT -> writeShort(it.value.data as Short)
                DATA_TYPE_LONG -> writeVarLong(it.value.data as Long)
            }
        }
    }


}