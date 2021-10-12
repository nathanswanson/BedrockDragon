package bedrockDragon.network.raknet.handler.packethandler.login

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.packethandler.PacketHandler
import io.netty.channel.Channel
import java.net.InetSocketAddress

class ConnectedPingHandler(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel): PacketHandler(channel) {
}