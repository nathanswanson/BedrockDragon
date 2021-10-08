package bedrockDragon.network.protocol.packethandler.connect

import bedrockDragon.DragonServer
import bedrockDragon.network.protocol.packethandler.EncapsulatedPacketHandler
import bedrockDragon.network.protocol.packethandler.PacketHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.Status
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel
import java.net.InetSocketAddress

class ConnectionRequestHandlerPost(override val sender: RakNetClientPeer, val packet: EncapsulatedPacket, channel : Channel) : EncapsulatedPacketHandler(sender, channel) {
    override fun responseToClient() {
        val connectionRequestPing = ConnectionRequest(packet.payload)
        connectionRequestPing.decode()

        val connectionRequestPong = ConnectionRequestAccepted()
        connectionRequestPong.clientAddress = sender.sender
        connectionRequestPong.clientTimestamp = connectionRequestPing.timestamp
        connectionRequestPong.serverTimestamp = sender.server.timeStamp()
        connectionRequestPong.encode()
        sender.status = Status.CONNECTED
        sendNettyMessage(Reliability.RELIABLE_ORDERED,0, connectionRequestPong)


    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S Client Connect Request"
    }

}