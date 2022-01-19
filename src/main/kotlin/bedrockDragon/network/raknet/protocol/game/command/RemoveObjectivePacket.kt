package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class RemoveObjectivePacket: PacketPayload(MinecraftPacketConstants.REMOVE_OBJECTIVE) {
    var objectiveId = ""

    override fun encode() {
        writeString(objectiveId)
    }
}