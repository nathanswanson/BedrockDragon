package bedrockDragon.network.raknet.handler.connect

import bedrockDragon.network.raknet.handler.PacketHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.RaknetConnectionStatus
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class DisconnectHandler(val sender: RakNetClientPeer, val packet: EncapsulatedPacket, channel : Channel) : PacketHandler(channel) {
    init {
        sender.raknetStatus = RaknetConnectionStatus.DISCONNECTED
    }
}