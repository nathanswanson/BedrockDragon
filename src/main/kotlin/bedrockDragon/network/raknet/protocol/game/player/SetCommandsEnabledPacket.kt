package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetCommandsEnabledPacket: PacketPayload(MinecraftPacketConstants.SET_COMMANDS_ENABLED) {
    var commandsEnabled = false

    override suspend fun encode() {
        writeBoolean(commandsEnabled)
    }
}