package bedrockDragon.network.raknet.handler.connect

import bedrockDragon.network.raknet.handler.EncapsulatedPacketHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.status.ConnectedPing
import bedrockDragon.network.raknet.protocol.status.ConnectedPong
import io.netty.channel.Channel

class ConnectedPingHandler(override val sender: RakNetPeer, val packet: EncapsulatedPacket, channel : Channel): EncapsulatedPacketHandler(sender, channel) {
    override fun responseToClient() {
        val ping = ConnectedPing(packet.payload)
        ping.decode()

        val pong = ConnectedPong()
        pong.timestamp = ping.timestamp
        pong.timestampPong = (sender as RakNetClientPeer).server.timeStamp()
        pong.encode()

        sender.sendMessage(Reliability.UNRELIABLE, 0 , pong)

    }
}