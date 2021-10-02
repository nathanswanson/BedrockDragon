package bedrockDragon.network.bedrockprotocol.packethandler

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted
import io.netty.channel.Channel
import java.net.InetSocketAddress

class ConnectionRequestHandlerPost(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel) : PacketHandler(channel) {
    override fun responseToClient() {
        val connectionRequestPing = ConnectionRequest(packet)
        connectionRequestPing.decode()

        val connectionRequestPong = ConnectionRequestAccepted()
        connectionRequestPong.clientAddress = sender
        connectionRequestPong.systemAddresses = systemAddresses
        connectionRequestPong.clientTimestamp = connectionRequestPing.timestamp
        connectionRequestPong.serverTimestamp = System.currentTimeMillis()

    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S Client Connect Request"
    }

    companion object {
        val systemAddresses = arrayOf(InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),
            InetSocketAddress("255.255.255.255", 19132),)
    }
}