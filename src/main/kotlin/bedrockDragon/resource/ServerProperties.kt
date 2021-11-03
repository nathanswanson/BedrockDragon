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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.resource

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

/**
 * global object which holds current server properties.
 * @author Nathan Swanson
 * @since ALPHA
 */
object ServerProperties: Properties() {
    init {
        try {
            load(FileReader("server.properties"))
        } catch (e: FileNotFoundException) {
            /*
                Generate properties file
             */
            //todo use bedrock properties not java
            setProperty("spawn-protection", "16")
            setProperty("max-tick-time", "60000")
            setProperty("query.port", "25565")
            setProperty("generator-settings", "")
            setProperty("sync-chunk-writes", "true")
            setProperty("force-gamemode", "false")
            setProperty("allow-nether", "true")
            setProperty("enforce-whitelist", "false")
            setProperty("gamemode", "survival")
            setProperty("broadcast-console-to-ops", "true")
            setProperty("enable-query", "false")
            setProperty("player-idle-timeout", "0")
            setProperty("difficulty", "easy")
            setProperty("spawn-monsters", "true")
            setProperty("broadcast-rcon-to-ops", "true")
            setProperty("op-permission-level", "4")
            setProperty("pvp", "true")
            setProperty("entity-broadcast-range-percentage", "100")
            setProperty("snooper-enabled", "true")
            setProperty("level-type", "default")
            setProperty("hardcore", "false")
            setProperty("enable-status", "true")
            setProperty("enable-command-block", "false")
            setProperty("max-players", "20")
            setProperty("network-compression-threshold", "256")
            setProperty("resource-pack-sha1", "")
            setProperty("max-world-size", "29999984")
            setProperty("function-permission-level", "2")
            setProperty("rcon.port", "25575")
            setProperty("server-port", "19132")
            setProperty("debug", "false")
            setProperty("server-ip", "")
            setProperty("spawn-npcs", "true")
            setProperty("allow-flight", "false")
            setProperty("level-name", "world")
            setProperty("view-distance", "10")
            setProperty("resource-pack", "")
            setProperty("spawn-animals" , "true")
            setProperty("white-list" , "false")
            setProperty("rcon.password", "")
            setProperty("generate-structures", "true")
            setProperty("max-build-height" , "256")
            setProperty("online-mode", "true")
            setProperty("level-seed", "")
            setProperty("use-native-transport" ,"true")
            setProperty("prevent-proxy-connections", "false")
            setProperty("enable-jmx-monitoring" , "false")
            setProperty("enable-rcon", "false")
            setProperty("motd", "A Minecraft Server")

            setProperty("dev-mode", "true")
            store(FileOutputStream("server.properties"), "Dragon Server")

        }
    }
}