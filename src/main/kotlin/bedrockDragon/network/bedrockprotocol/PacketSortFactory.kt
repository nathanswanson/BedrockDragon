package bedrockDragon.network.bedrockprotocol

import bedrockDragon.network.bedrockprotocol.packethandler.LoginHandler
import bedrockDragon.network.bedrockprotocol.packethandler.PacketHandler
import bedrockDragon.network.raknet.RakNetPacket
import io.netty.channel.Channel
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

class PacketSortFactory {
    companion object {
        fun createPacketHandle(sender: InetSocketAddress, packet: RakNetPacket, channel: Channel) : PacketHandler {
            val id = packet.id.toInt()

            return when(id) {
                1 -> LoginHandler(sender, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }
        }
    }
}