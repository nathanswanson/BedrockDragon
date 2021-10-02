package bedrockDragon.network.bedrockprotocol.packethandler

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestOne
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestTwo
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseOne
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseTwo
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.*

class ConnectionRequestHandlerTwo(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel) : PacketHandler(channel) {
    override fun responseToClient() {
        //Must check if able to join not banned, server not full, etc
        val connectionRequestPing = OpenConnectionRequestTwo(packet)
        connectionRequestPing.decode()

        val connectionRequestPong = OpenConnectionResponseTwo()
        connectionRequestPong.serverGuid = UUID.randomUUID().leastSignificantBits
        connectionRequestPong.clientAddress = sender
        connectionRequestPong.maximumTransferUnit = connectionRequestPing.maximumTransferUnit
        connectionRequestPong.encryptionEnabled = false
        connectionRequestPong.encode()

        sendNettyMessage(connectionRequestPong, sender)
    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S HandShake 2"
    }
}