package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class AddEntityPacket: PacketPayload(MinecraftPacketConstants.ADD_ENTITY) {
    var entitySelfId = -1L //sVarLong
    var runtimeEntityId = -1L //varLong
    var entityType = ""
    lateinit var position: Float3
    lateinit var velocity: Float3
    lateinit var rotation: Float3
    //attribute
    //metadata
    //links


}