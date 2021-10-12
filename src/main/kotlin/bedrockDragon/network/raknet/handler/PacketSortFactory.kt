package bedrockDragon.network.raknet.handler

import bedrockDragon.debug.clientSimulator.packet.ConnectionRequestAcceptedHandler
import bedrockDragon.network.raknet.handler.packethandler.connect.ConnectionRequestHandlerPost
import bedrockDragon.network.raknet.handler.packethandler.connect.IncomingConnectionHandler
import bedrockDragon.network.raknet.handler.packethandler.login.ConnectionRequestHandlerOne
import bedrockDragon.network.raknet.handler.packethandler.login.ConnectionRequestHandlerTwo
import bedrockDragon.network.raknet.handler.packethandler.login.LoginHandler
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.handler.packethandler.PacketHandler
import bedrockDragon.network.raknet.handler.packethandler.login.ConnectedPingHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import io.netty.channel.Channel
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

class PacketSortFactory {
    companion object {
        fun createPacketHandle(sender: InetSocketAddress, packet: RakNetPacket, channel: Channel) : PacketHandler {
            return when(packet.id.toInt()) {
                PacketConstants.CONNECTED_PING -> ConnectedPingHandler(sender, packet, channel)
                PacketConstants.UNCONNECTED_PING -> LoginHandler(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_1 -> ConnectionRequestHandlerOne(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_2 -> ConnectionRequestHandlerTwo(sender, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }
        }

        fun createClientPacketHandle(sender: RakNetPeer, packet: EncapsulatedPacket, channel: Channel) : PacketHandler {
            return when(packet.payload.buffer().getUnsignedByte(0).toInt()) {
                PacketConstants.CONNECTION_REQUEST -> ConnectionRequestHandlerPost(sender, packet, channel)
                PacketConstants.NEW_INCOMING_CONNECTION -> IncomingConnectionHandler(sender as RakNetClientPeer, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }

        }

        //CLIENT PACKETS FOR CLIENT SIMULATION

        fun createOutboundClientHandler(sender: RakNetPeer, packet: EncapsulatedPacket, channel: Channel) : PacketHandler {
            return when(packet.payload.buffer().getUnsignedByte(0).toInt()) {
                PacketConstants.CONNECTION_REQUEST_ACCEPTED -> ConnectionRequestAcceptedHandler(sender, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }
        }
    }
}