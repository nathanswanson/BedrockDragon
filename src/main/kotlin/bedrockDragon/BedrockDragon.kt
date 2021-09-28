package bedrockDragon

import com.nukkitx.protocol.bedrock.*
import com.nukkitx.protocol.bedrock.v465.Bedrock_v465
import java.net.InetSocketAddress


fun main() {
    val bindAddress = InetSocketAddress("0.0.0.0", 19132)
    val server = BedrockServer(bindAddress)

    val pong = BedrockPong()
    pong.edition = "MCPE"
    pong.motd = "My Server"
    pong.playerCount = 0
    pong.maximumPlayerCount = 20
    pong.gameType = "Survival"
    pong.protocolVersion = Bedrock_v465.V465_CODEC.protocolVersion

    server.handler = object : BedrockServerEventHandler {
        override fun onConnectionRequest(address: InetSocketAddress): Boolean {
            return true // Connection will be accepted
        }

        override fun onQuery(address: InetSocketAddress): BedrockPong? {
            return pong
        }

        override fun onSessionCreation(serverSession: BedrockServerSession) {
            // Connection established
            // Add disconnect handler
            serverSession.addDisconnectHandler { reason -> println("Disconnected") }
            // Remember to set a packet handler so you receive incoming packets
            //serverSession.setPacketHandler(handler)
            // By default, the server will use a compatible codec that will read any LoginPacket.
            // After receiving the LoginPacket, you need to set the correct packet codec for the client and continue.
        }
    }

    server.bind().join()

    val clientAddress = InetSocketAddress("0.0.0.0", 12345)
    val client = BedrockClient(clientAddress)

    client.bind().join()
    //Main server tick
    while(true) {
        println(server.bootstrap)
    }

}