package bedrockDragon.network.raknet.protocol.game.util

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class TickSyncPacket: PacketPayload(MinecraftPacketConstants.TICK_SYNC) {
    var requestTimestamp = -1L
    var responseTimestamp = -1L

    override fun encode() {
        writeLong(requestTimestamp)
        writeLong(responseTimestamp)
    }

    override fun decode(packet: Packet) {
        requestTimestamp = packet.readLong()
        responseTimestamp = packet.readLong()
    }
}