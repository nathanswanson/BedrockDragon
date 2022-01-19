package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class RespawnPacket: PacketPayload(MinecraftPacketConstants.RESPAWN) {
    lateinit var position: Float3
    var state = -1 //byte
    var runtimeEntityId = -1L //vLong

    override fun encode() {
    }
}