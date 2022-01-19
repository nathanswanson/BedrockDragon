package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class NetworkStackLatencyPacket: PacketPayload(MinecraftPacketConstants.NETWORK_STACK_LATENCY) {
    var timeStamp = 1uL
    var sendBack = true

    override fun encode() {
        writeUnsignedLong(timeStamp.toLong())
        writeBoolean(sendBack)
    }

    override fun decode(packet: Packet) {
        timeStamp = packet.readUnsignedLong().longValueExact().toULong()
        sendBack = packet.readBoolean()
    }
}