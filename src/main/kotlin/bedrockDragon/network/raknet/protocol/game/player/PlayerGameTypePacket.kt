package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class PlayerGameTypePacket: PacketPayload(MinecraftPacketConstants.SET_PLAYER_GAME_TYPE) {
    var gamemode = -1 //sVarInt

    override fun encode() {
        writeVarInt(gamemode)
    }
}