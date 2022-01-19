package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AvailableCommandsPacket: PacketPayload(MinecraftPacketConstants.AVAILABLE_COMMANDS) {
}