package bedrockDragon.network.raknet.protocol.game.util

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class CameraPacket: PacketPayload(MinecraftPacketConstants.CAMERA) {
    var cameraUniqueEntityId = -1L //sVarLong
    var playerUniqueEntityId = -1L //sVarLong

    override suspend fun encode() {
        writeVarLong(cameraUniqueEntityId)
        writeVarLong(playerUniqueEntityId)
    }
}