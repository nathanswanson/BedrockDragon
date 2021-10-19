package bedrockDragon.network.raknet.handler.handlertype.login

import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketHandler
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestTwo
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseTwo
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

class ConnectionRequestHandlerTwo(val sender: InetSocketAddress, val packet: RakNetPacket, channel : Channel,
                                  private val guid: Long,private val mtu: Int) : PacketHandler(channel) {

    lateinit var connectionType: ConnectionType
    var clientGuid = 0L
    var clientmtu = 0

    override fun responseToClient() {
        //Must check if able to join not banned, server not full, etc
        val connectionRequestPing = OpenConnectionRequestTwo(packet)
        connectionRequestPing.decode()

        if(connectionRequestPing.maximumTransferUnit >= RakNet.MINIMUM_MTU_SIZE) {
            val connectionRequestPong = OpenConnectionResponseTwo()
            connectionRequestPong.serverGuid = guid
            connectionRequestPong.clientAddress = sender
            connectionRequestPong.maximumTransferUnit =
                connectionRequestPing.maximumTransferUnit.coerceAtMost(mtu)
            connectionRequestPong.encryptionEnabled = false

            clientmtu = connectionRequestPong.maximumTransferUnit
            clientGuid = connectionRequestPing.clientGuid
            connectionType = connectionRequestPing.connectionType!!
            connectionRequestPong.encode()

            channel.writeAndFlush(DatagramPacket(connectionRequestPong.buffer(), sender))
            finished = true
        } else {
            logger.info { "Failed MTU too small for response" }
        }
    }

    override fun responseToServer() {
        //NO RESPONSE
    }

    override fun toString(): String {
        return "C->S HandShake 2"
    }
}