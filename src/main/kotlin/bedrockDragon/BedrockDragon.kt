package bedrockDragon

import java.net.InetSocketAddress
import mu.KotlinLogging
import java.io.*
import java.util.*

private val logger = KotlinLogging.logger {}

fun main() {
    val bindAddress = InetSocketAddress("0.0.0.0", 19132)
    val server = DragonServer(bindAddress)

    logger.info { "Starting Bedrock Dragon." }
    logger.info { "Validating server root." }

    val directories = arrayOf("mods","world","logs","players","config")
    for(dir in directories) {
        if (!File(dir).isDirectory) {
            File(dir).mkdir()
        }
    }

    logger.info { "Loading server properties." }
    val serverPropeties = Properties()
    try {
        serverPropeties.load(FileReader("server.properties"))
    } catch (e: FileNotFoundException) {
        /*
            Generate properties file
         */
        serverPropeties.setProperty("spawn-protection", "16")
        serverPropeties.setProperty("max-tick-time", "60000")
        serverPropeties.setProperty("query.port", "25565")
        serverPropeties.setProperty("generator-settings", "")
        serverPropeties.setProperty("sync-chunk-writes", "true")
        serverPropeties.setProperty("force-gamemode", "false")
        serverPropeties.setProperty("allow-nether", "true")
        serverPropeties.setProperty("enforce-whitelist", "false")
        serverPropeties.setProperty("gamemode", "survival")
        serverPropeties.setProperty("broadcast-console-to-ops", "true")
        serverPropeties.setProperty("enable-query", "false")
        serverPropeties.setProperty("player-idle-timeout", "0")
        serverPropeties.setProperty("difficulty", "easy")
        serverPropeties.setProperty("spawn-monsters", "true")
        serverPropeties.setProperty("broadcast-rcon-to-ops", "true")
        serverPropeties.setProperty("op-permission-level", "4")
        serverPropeties.setProperty("pvp", "true")
        serverPropeties.setProperty("entity-broadcast-range-percentage", "100")
        serverPropeties.setProperty("snooper-enabled", "true")
        serverPropeties.setProperty("level-type", "default")
        serverPropeties.setProperty("hardcore", "false")
        serverPropeties.setProperty("enable-status", "true")
        serverPropeties.setProperty("enable-command-block", "false")
        serverPropeties.setProperty("max-players", "20")
        serverPropeties.setProperty("network-compression-threshold", "256")
        serverPropeties.setProperty("resource-pack-sha1", "")
        serverPropeties.setProperty("max-world-size", "29999984")
        serverPropeties.setProperty("function-permission-level", "2")
        serverPropeties.setProperty("rcon.port", "25575")
        serverPropeties.setProperty("server-port", "25565")
        serverPropeties.setProperty("debug", "false")
        serverPropeties.setProperty("server-ip", "")
        serverPropeties.setProperty("spawn-npcs", "true")
        serverPropeties.setProperty("allow-flight", "false")
        serverPropeties.setProperty("level-name", "world")
        serverPropeties.setProperty("view-distance", "10")
        serverPropeties.setProperty("resource-pack", "")
        serverPropeties.setProperty("spawn-animals" , "true")
        serverPropeties.setProperty("white-list" , "false")
        serverPropeties.setProperty("rcon.password", "")
        serverPropeties.setProperty("generate-structures", "true")
        serverPropeties.setProperty("max-build-height" , "256")
        serverPropeties.setProperty("online-mode", "true")
        serverPropeties.setProperty("level-seed", "")
        serverPropeties.setProperty("use-native-transport" ,"true")
        serverPropeties.setProperty("prevent-proxy-connections", "false")
        serverPropeties.setProperty("enable-jmx-monitoring" , "false")
        serverPropeties.setProperty("enable-rcon", "false")
        serverPropeties.setProperty("motd", "A Minecraft Server")
        serverPropeties.store(FileOutputStream("server.properties"), "Dragon Server")

    }

    logger.info { "Loading world." }
    if (File("mods").listFiles().isEmpty()) {
        logger.warn { "World not found. Generating..." }
    }

    logger.info { "Registering mods." }
    registerMods()

    logger.info { "=====================" }
    logger.info { "SERVER PRE-INIT DONE." }
    logger.info { "STARTING DRAGON SERVER." }
    logger.info { "=====================" }

    server.start()
}

fun registerMods() {

}
