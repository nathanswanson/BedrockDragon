package bedrockDragon.network.raknet.protocol.game.event

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class ScriptCustomEventPacket: PacketPayload(MinecraftPacketConstants.SCRIPT_CUSTOM_EVENT) {
    var eventName = ""
    var data = "" //json

    override suspend fun encode() {
        writeString(eventName)
        writeString(data)
    }

    override fun decode(packet: Packet) {
        eventName = packet.readString()
        data = packet.readString()
    }
}