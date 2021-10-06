package bedrockDragon.network.protocol.packethandler

import bedrockDragon.DragonServer
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestOne
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseOne
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.*

class ConnectionRequestHandlerOne(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel) : PacketHandler(channel) {
    override fun responseToClient() {
        //Must check if able to join not banned, server not full, etc
        val connectionRequestPing = OpenConnectionRequestOne(packet)
        connectionRequestPing.decode()

        val connectionRequestPong = OpenConnectionResponseOne()
        connectionRequestPong.serverGuid = DragonServer.guid
        connectionRequestPong.useSecurity = false
        connectionRequestPong.maximumTransferUnit = connectionRequestPing.maximumTransferUnit
        connectionRequestPong.encode()

        sendNettyMessage(connectionRequestPong, sender)
        finished = true

    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S HandShake 1"
    }
}