package bedrockDragon

import java.net.InetSocketAddress
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    val bindAddress = InetSocketAddress("0.0.0.0", 19132)
    val server = DragonServer(bindAddress)

    logger.info { "Starting Bedrock Dragon" }

    server.start()
}