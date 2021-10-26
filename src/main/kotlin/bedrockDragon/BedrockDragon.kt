/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon

import bedrockDragon.mod.loader.ModLoader
import bedrockDragon.resource.ResourcePackManager
import java.net.InetSocketAddress
import mu.KotlinLogging
import java.io.*
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Welcome to the very start of the server. main checks if root directory
 * is in order, loads mods, and reads server.properties
 *
 * If the server is pre-loaded properly we then initialize DragonServer()
 *
 * @author Nathan Swanson
 * @since Bedrock Dragon ALPHA
 */
fun main() {


    logger.info { "Starting Bedrock Dragon." }
    logger.info { "Validating server root." }

    val directories = arrayOf("mods","world","logs","players","config")
    for(dir in directories) {
        if (!File(dir).isDirectory) {
            File(dir).mkdir()
        }
    }

    logger.info { "Loading server properties." }

    val serverProperties = Properties()
    try {
        serverProperties.load(FileReader("server.properties"))
    } catch (e: FileNotFoundException) {
        /*
            Generate properties file
         */
        //todo use bedrock properties not java
        serverProperties.setProperty("spawn-protection", "16")
        serverProperties.setProperty("max-tick-time", "60000")
        serverProperties.setProperty("query.port", "25565")
        serverProperties.setProperty("generator-settings", "")
        serverProperties.setProperty("sync-chunk-writes", "true")
        serverProperties.setProperty("force-gamemode", "false")
        serverProperties.setProperty("allow-nether", "true")
        serverProperties.setProperty("enforce-whitelist", "false")
        serverProperties.setProperty("gamemode", "survival")
        serverProperties.setProperty("broadcast-console-to-ops", "true")
        serverProperties.setProperty("enable-query", "false")
        serverProperties.setProperty("player-idle-timeout", "0")
        serverProperties.setProperty("difficulty", "easy")
        serverProperties.setProperty("spawn-monsters", "true")
        serverProperties.setProperty("broadcast-rcon-to-ops", "true")
        serverProperties.setProperty("op-permission-level", "4")
        serverProperties.setProperty("pvp", "true")
        serverProperties.setProperty("entity-broadcast-range-percentage", "100")
        serverProperties.setProperty("snooper-enabled", "true")
        serverProperties.setProperty("level-type", "default")
        serverProperties.setProperty("hardcore", "false")
        serverProperties.setProperty("enable-status", "true")
        serverProperties.setProperty("enable-command-block", "false")
        serverProperties.setProperty("max-players", "20")
        serverProperties.setProperty("network-compression-threshold", "256")
        serverProperties.setProperty("resource-pack-sha1", "")
        serverProperties.setProperty("max-world-size", "29999984")
        serverProperties.setProperty("function-permission-level", "2")
        serverProperties.setProperty("rcon.port", "25575")
        serverProperties.setProperty("server-port", "19132")
        serverProperties.setProperty("debug", "false")
        serverProperties.setProperty("server-ip", "")
        serverProperties.setProperty("spawn-npcs", "true")
        serverProperties.setProperty("allow-flight", "false")
        serverProperties.setProperty("level-name", "world")
        serverProperties.setProperty("view-distance", "10")
        serverProperties.setProperty("resource-pack", "")
        serverProperties.setProperty("spawn-animals" , "true")
        serverProperties.setProperty("white-list" , "false")
        serverProperties.setProperty("rcon.password", "")
        serverProperties.setProperty("generate-structures", "true")
        serverProperties.setProperty("max-build-height" , "256")
        serverProperties.setProperty("online-mode", "true")
        serverProperties.setProperty("level-seed", "")
        serverProperties.setProperty("use-native-transport" ,"true")
        serverProperties.setProperty("prevent-proxy-connections", "false")
        serverProperties.setProperty("enable-jmx-monitoring" , "false")
        serverProperties.setProperty("enable-rcon", "false")
        serverProperties.setProperty("motd", "A Minecraft Server")

        serverProperties.setProperty("dev-mode", "true")
        serverProperties.store(FileOutputStream("server.properties"), "Dragon Server")

    }
    if(serverProperties.getProperty("dev-mode").toBoolean())
        logger.info { "Warning dev mode enabled." }

    logger.info { "Loading world." }
    //Todo unsafe
    if (File("mods").listFiles().isEmpty()) {
        logger.warn { "World not found. Generating..." }
    }

    logger.info { "Registering mods." }
    registerMods()

    logger.info { "Registering blocks." }

    ResourcePackManager


    logger.info { "=====================" }
    logger.info { "SERVER PRE-INIT DONE." }
    logger.info { "STARTING DRAGON SERVER." }
    logger.info { "=====================" }

    val bindAddress = InetSocketAddress(
        serverProperties.getProperty("server-ip"),
        serverProperties.getProperty("server-port").toInt()
    )

    //val motd = MinecraftPeer()

    val server = DragonServer(bindAddress)
    server.start()
}


/**
 * Initialize Mod Manager
 *
 * @author Nathan Swanson
 * @since Bedrock Dragon ALPHA
 */
fun registerMods() {
    val mods = ModLoader.getModfolderContent()
    //remove any non jars from list
    //mods.filter { mod -> ModManager.authenticate(mod) }

}
