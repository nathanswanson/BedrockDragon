package bedrockDragon.network.raknet.handler.packethandler.connect

import bedrockDragon.network.raknet.handler.packethandler.PacketHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.ConnectionStatus
import bedrockDragon.network.raknet.protocol.login.NewIncomingConnection
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class IncomingConnectionHandler(val sender: RakNetClientPeer, val packet: EncapsulatedPacket, channel : Channel) : PacketHandler(channel) {

    override fun responseToClient() {
        val incoming = NewIncomingConnection(packet.payload)
        incoming.decode()
        sender.status = ConnectionStatus.CONNECTED
        logger.info { "$sender has successfully joined the server." }
    }


}