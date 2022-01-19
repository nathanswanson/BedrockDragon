package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.util.*

class PlayerSkinPacket: PacketPayload(MinecraftPacketConstants.PLAYER_SKIN) {
    lateinit var uuid: UUID
    //skin
    var skinName = ""
    var oldSkinName = ""
    //todo
}