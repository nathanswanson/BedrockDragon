package bedrockDragon.network.raknet.protocol.packet.packethandler.connect

import bedrockDragon.network.raknet.protocol.packet.packethandler.EncapsulatedPacketHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.peer.Status
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class ConnectionRequestHandlerPost(override val sender: RakNetPeer, val packet: EncapsulatedPacket, channel : Channel) : EncapsulatedPacketHandler(sender, channel) {
    override fun responseToClient() {
        if(sender is RakNetClientPeer) {
            val connectionRequestPing = ConnectionRequest(packet.payload)
            connectionRequestPing.decode()

            val connectionRequestPong = ConnectionRequestAccepted()
            connectionRequestPong.clientAddress = sender.address
            connectionRequestPong.clientTimestamp = connectionRequestPing.timestamp
            connectionRequestPong.serverTimestamp = sender.server.timeStamp()
            connectionRequestPong.encode()
            sender.status = Status.CONNECTED
            sendNettyMessage(Reliability.RELIABLE_ORDERED, 0, connectionRequestPong)

        }
    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S Client Connect Request"
    }

}