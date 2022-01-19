package bedrockDragon.network.raknet.handler

import bedrockDragon.network.raknet.peer.RakNetPeer
import io.netty.channel.Channel

abstract class EncapsulatedPacketHandler(open val sender: RakNetPeer, channel : Channel) : PacketHandler(channel) {
}