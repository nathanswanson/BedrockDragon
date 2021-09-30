package bedrockDragon

import protocol.bedrock.*
import java.net.InetSocketAddress
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    val bindAddress = InetSocketAddress("0.0.0.0", 19132)
    val server = Server(bindAddress)

    val pong = BedrockPong()
    pong.edition = "MCPE"
    pong.motd = "My Server"
    pong.playerCount = 0
    pong.maximumPlayerCount = 20
    pong.gameType = "Survival"
   // pong.protocolVersion = protocol.bedrock.

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


    logger.info { "Starting Bedrock Dragon" }

    server.start()
}