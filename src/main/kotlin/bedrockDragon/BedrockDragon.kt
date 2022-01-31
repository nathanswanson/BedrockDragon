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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon

import bedrockDragon.mod.loader.ModLoader
import bedrockDragon.registry.Registry
import bedrockDragon.registry.resource.NativeCommands
import bedrockDragon.resource.ResourcePackManager
import bedrockDragon.resource.ServerProperties
import bedrockDragon.world.World
import java.net.InetSocketAddress
import mu.KotlinLogging
import java.io.*

private val logger = KotlinLogging.logger {}

const val SERVER_VERSION = "ALPHA"

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


    logger.info { "Starting Bedrock Dragon." }
    logger.info { "Validating server root." }

    val directories = arrayOf("mods","world","logs","players","config")
    for(dir in directories) {
        if (!File(dir).isDirectory) {
            File(dir).mkdir()
        }
    }

    logger.info { "Loading server properties." }


    if(ServerProperties.getProperty("dev-mode").toBoolean())
        logger.info { "Warning dev mode enabled." }

    logger.info { "Loading world." }
    if (File("world").listFiles().isEmpty()) {
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

//    val bindAddress = InetSocketAddress(
//        ServerProperties.getProperty("server-ip"),
//        ServerProperties.getProperty("server-port").toInt()
//    )

    //register commands
    NativeCommands

    //todo temp
    val bindAddress = InetSocketAddress(19132)
    //world registry
    Registry.WORLD_REGISTRY.register(0, World("noOcean"))
    logger.info { bindAddress }
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
