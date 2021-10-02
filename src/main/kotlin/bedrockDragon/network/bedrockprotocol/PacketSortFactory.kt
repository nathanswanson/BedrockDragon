package bedrockDragon.network.bedrockprotocol

import bedrockDragon.network.bedrockprotocol.packethandler.*
import bedrockDragon.network.raknet.RakNetPacket
import io.netty.channel.Channel
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

class PacketSortFactory {
    companion object {
        fun createPacketHandle(sender: InetSocketAddress, packet: RakNetPacket, channel: Channel) : PacketHandler {
            val id = packet.id.toInt()

            return when(id) {

                PacketConstants.LOGIN_PACKET -> LoginHandler(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_1 -> ConnectionRequestHandlerOne(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_2 -> ConnectionRequestHandlerTwo(sender, packet, channel)
                PacketConstants.CONNECTION_REQUEST -> ConnectionRequestHandlerPost(sender, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }
        }
    }
}