package bedrockDragon.network.raknet.handler.handlertype.login

import bedrockDragon.DragonServer
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketHandler
import bedrockDragon.network.raknet.identifier.Identifier
import bedrockDragon.network.raknet.protocol.status.UnconnectedPing
import bedrockDragon.network.raknet.protocol.status.UnconnectedPong
import bedrockDragon.network.raknet.server.ServerPing
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

class LoginHandler(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel, private val pongId: Long) : PacketHandler(channel) {


    override fun responseToClient() {

        val unconnectedPing = UnconnectedPing(packet)
        unconnectedPing.decode()

        if(!unconnectedPing.failed() && unconnectedPing.magic) {
            val identifier = Identifier("bedrockdragon")
            val pingEvent = ServerPing(sender, unconnectedPing.connectionType!!, identifier)


            val pong = UnconnectedPong()
            pong.timestamp = unconnectedPing.timestamp
            pong.pongId = pongId
            pong.identifier = pingEvent.identifier
            pong.encode()
            if(!pong.failed()) {
                channel.writeAndFlush(DatagramPacket(pong.buffer(), sender))
                finished = true
            } else {
                logger.info { "Failed to send back response Packet" }
            }
        }
    }

    override fun responseToServer() {
         //NO RESPONSE

    }


    override fun toString(): String {
        return "Login Handle"
    }

}

