package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class UpdateSoftEnumPacket: PacketPayload(MinecraftPacketConstants.UPDATE_SOFT_ENUM) {
    //todo enumData
    var action: Byte = 0
}