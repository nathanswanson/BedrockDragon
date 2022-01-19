package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetScorePacket: PacketPayload(MinecraftPacketConstants.SET_SCORE) {
    var action: Byte = 0 //0 add 1 remove
    //todo entries
}