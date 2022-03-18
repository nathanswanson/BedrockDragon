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




}