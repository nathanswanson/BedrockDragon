package bedrockDragon.network.raknet.protocol.game.ui

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class FilterText: PacketPayload(MinecraftPacketConstants.FILTER_TEXT) {
    var text = ""
    var fromServer = true

    override fun encode() {
        writeString(text)
        writeBoolean(fromServer)
    }

    override fun decode(packet: Packet) {
        text = packet.readString()
        fromServer = packet.readBoolean()
    }
}