package bedrockDragon.network.raknet.protocol.game.util

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class VideoStreamConnectPacket: PacketPayload(MinecraftPacketConstants.VIDEO_STREAM_CONNECT) {
    var serverURI = ""
    var fps: Float = 0f
    var action: Byte = 0
    var resolutionX = 0
    var resolutionY = 0

    override suspend fun encode() {
        writeString(serverURI)
        writeFloat(fps)
        writeByte(action)
        writeInt(resolutionX)
        writeInt(resolutionY)
    }
}