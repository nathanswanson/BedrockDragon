package bedrockDragon.network.raknet.protocol.packet.packethandler

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

open class EncapsulatedPacketHandler(open val sender: RakNetPeer, channel : Channel) : PacketHandler(channel) {
}