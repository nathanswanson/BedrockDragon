package bedrockDragon.network.raknet.protocol.game.event

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SimpleEventPacket: PacketPayload(MinecraftPacketConstants.SIMPLE_EVENT) {
}