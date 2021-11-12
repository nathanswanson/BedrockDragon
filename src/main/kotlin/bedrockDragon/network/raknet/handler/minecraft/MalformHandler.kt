package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.handler.MinecraftHandler
import bedrockDragon.network.raknet.protocol.game.util.MalformPacket
import mu.KotlinLogging

class MalformHandler(packet: Packet): MinecraftHandler() {

    val logger = KotlinLogging.logger {}


    init {
        val malformPacket = MalformPacket()
        malformPacket.decode(packet)


        logger.error { """MALFORM ERROR OF 
            TYPE: ${malformPacket.type}
            SEVERITY: ${malformPacket.severity}
            MESSAGE: ${malformPacket.context}   
        """.trimMargin() }
    }
    override fun pass() {

    }
}