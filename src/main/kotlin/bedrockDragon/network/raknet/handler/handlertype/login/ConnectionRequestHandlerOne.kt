package bedrockDragon.network.raknet.handler.handlertype.login

import bedrockDragon.DragonServer
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketHandler
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestOne
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseOne
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

class ConnectionRequestHandlerOne(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel, val guid : Long) : PacketHandler(channel) {
    override fun responseToClient() {
        //Must check if able to join not banned, server not full, etc
        val connectionRequestPing = OpenConnectionRequestOne(packet)
        connectionRequestPing.decode()

        val connectionRequestPong = OpenConnectionResponseOne()
        connectionRequestPong.serverGuid = guid
        connectionRequestPong.useSecurity = false
        connectionRequestPong.maximumTransferUnit = connectionRequestPing.maximumTransferUnit
        connectionRequestPong.encode()

        channel.writeAndFlush(DatagramPacket(connectionRequestPong.buffer(), sender))
        finished = true

    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S HandShake 1"
    }
}