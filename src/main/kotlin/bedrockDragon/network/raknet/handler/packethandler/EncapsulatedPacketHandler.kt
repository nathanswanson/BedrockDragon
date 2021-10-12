package bedrockDragon.network.raknet.handler.packethandler

import bedrockDragon.network.raknet.peer.RakNetPeer
import io.netty.channel.Channel

open class EncapsulatedPacketHandler(open val sender: RakNetPeer, channel : Channel) : PacketHandler(channel) {
}