package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class SetDisplayObjectivePacket: PacketPayload(MinecraftPacketConstants.SET_DISPLAY_OBJECTIVE) {
    var displaySlot = ""
    var objectiveId = ""
    var displayName = ""
    var criteria = ""
    var sortOrder = -1 //sVarInt

    override suspend fun encode() {
        writeString(displaySlot)
        writeString(objectiveId)
        writeString(displayName)
        writeString(criteria)
        writeVarInt(sortOrder)
    }
}