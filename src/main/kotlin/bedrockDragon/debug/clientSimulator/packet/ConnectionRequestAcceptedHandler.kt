package bedrockDragon.debug.clientSimulator.packet

import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.packet.packethandler.EncapsulatedPacketHandler
import io.netty.channel.Channel

class ConnectionRequestAcceptedHandler(override val sender: RakNetPeer, val packet: EncapsulatedPacket, channel : Channel): EncapsulatedPacketHandler(sender, channel) {
}