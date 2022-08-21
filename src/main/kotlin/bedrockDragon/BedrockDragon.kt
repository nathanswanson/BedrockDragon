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
 * Copyright (c) 2021-2022 Nathan Swanson
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon

import bedrockDragon.mod.Mod
import bedrockDragon.registry.Registry
import bedrockDragon.resource.ServerProperties
import bedrockDragon.util.text.GOLD
import bedrockDragon.util.text.GREEN
import bedrockDragon.util.text.ITALIC
import bedrockDragon.util.text.RED
import bedrockDragon.world.PaletteGlobal
import bedrockDragon.world.World
import java.net.InetSocketAddress
import mu.KotlinLogging
import java.io.*
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.name

private val logger = KotlinLogging.logger {}

const val SERVER_VERSION = "ALPHA"
const val MINECRAFT_VERSION = "1.19.20"
const val PROTOCOL_VERSION = "544"
/**
 * Welcome to the very start of the server. main checks if root directory
 * is in order, loads mods, and reads server.properties
 *
 * If the server is pre-loaded properly we then initialize DragonServer()
 *
 * @author Nathan Swanson
 * @since Bedrock Dragon ALPHA
 */
fun main(args: Array<String>) {


    println(ClassLoader.getSystemResource("logo.txt").readText())

    logger.info { "Starting Bedrock Dragon v$SERVER_VERSION for bedrock $MINECRAFT_VERSION($PROTOCOL_VERSION)".GREEN() }
    //configure phase

    logger.info { "Configuring server".GOLD() }
    val directories = arrayOf("mods","world","logs","players","config")
    for(dir in directories) {
        if (!File(dir).isDirectory) {
            File(dir).mkdir()
        }
    }

    logger.info { "Loading server properties"}

    if(ServerProperties.getProperty("dev-mode").toBoolean())
        logger.info { "Warning dev mode enabled in properties".RED() }

    //bind phase
    val bindAddress = if (ServerProperties.getProperty("server-ip").isBlank())
        InetSocketAddress(ServerProperties.getProperty("server-port").toInt()) else
        InetSocketAddress(ServerProperties.getProperty("server-ip"), ServerProperties.getProperty("server-port").toInt()
    )
    logger.info { "Server will run on $bindAddress" }


    //register phase
    logger.info { "Starting registry".GOLD() }
    Registry.WORLD_REGISTRY.register(0, World(ServerProperties.getOrDefault("level-name", "world") as String))
    PaletteGlobal

    logger.info { "Loading world(s)"}


    //world phase
    if (File(ServerProperties.getProperty("level-name")).listFiles()?.isEmpty() == true) {
        logger.warn { "World not found. Generating..." }
    }
    logger.info { "Default world found: ${Registry.WORLD_REGISTRY[0]}" }



    //mod phase
    logger.info { "Registering mods".GOLD() }
    registerMods()

    logger.info { "Loaded ${Registry.MOD_REGISTRY.size()} mods" }

    logger.info { "Loaded ${Registry.COMMAND_REGISTRY.size()} commands" }
    //VanillaEntities
    logger.info { "Loaded ${Registry.ENTITY_REGISTRY.size()} entities" }
    //VanillaBlocks
    logger.info { "Loaded ${PaletteGlobal.blockRegistry.size} blocks" }
    //VanillaItems
    logger.info { "Loaded ${Registry.ITEM_REGISTRY.size()} items" }

    //ResourcePackManager


    logger.info { "Load complete".GREEN() }
    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

    DragonServer(bindAddress).start()
}


/**
 * Initialize Mod Manager
 *
 * @author Nathan Swanson
 * @since Bedrock Dragon ALPHA
 */
fun registerMods() {
    val files = Files.walk(Path("mods"),1).filter{ it.name != "mods"}

    files.forEach {
        val mod = Mod(it)
        Registry.MOD_REGISTRY[mod.config.name] = mod

        logger.info { "Registering mod: ${mod.config.name} - " + mod.config.id.ITALIC() + "v${mod.config.version}"  }
        mod.compile()

    }
}

