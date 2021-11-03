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

package bedrockDragon.mod.loader

import bedrockDragon.mod.DragonMod
import bedrockDragon.mod.Mod
import bedrockDragon.mod.informative.ModDoc
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException
import java.net.URLClassLoader
import java.nio.file.Files

@Singleton
object ModLoader {

    private val logger = KotlinLogging.logger {}

    fun getModfolderContent(): Array<File> {
        return try {
            File("mods").listFiles()!!
        } catch (e: FileNotFoundException) {
            logger.info { e }
            emptyArray()
        }
    }

    /**
     * When reloading plugins instead of restarting completely
     * the hash can tell us if the mod was truly different and
     * actually needs to be reloaded
     * @author Nathan Swanson
     * @since ALPHA
     */

    private fun getModHash(): Int {
        return TODO()
    }

    private fun safeUnload(vararg mods: Mod) {

    }

    private fun safeLoad(vararg mods: File): Mod {
        for (mod in mods) {
            if (authenticate(mod)) {
                generateDetachedJar(mod)
            }
        }

        return TODO()
    }

    private fun generateDetachedJar(mod: File): JarContents {
        val loader = URLClassLoader(arrayOf(mod.toURI().toURL()))
        val classes = ArrayList<Class<*>>()
        Files.walk(mod.toPath()).forEach { file ->
            if (file.endsWith(".class")) {
                val tempClass = loader.loadClass(file.toString())
                if(tempClass.isAnnotationPresent(DragonMod::class.java)) {
                    classes.add(tempClass)
                }
            }
        }
        return TODO()
    }

    fun authenticate(mod: File): Boolean {
        /* mod is authenticated under the following conditions
            -Is a jar file, folder, or zip (which all follow the same Dragon Mod format)
            -has plugin config
            -Mod version is supported by the current server
            -Contains a Dragon Mod main file annotated with @DragonMod //CHECKED ON GENERATION NOT HERE
            -Passes dependency check.
         */

        //file check
        if(!(mod.isDirectory or mod.name.endsWith("zip") or mod.name.endsWith("jar"))) {
            return false
        }
        return true
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun parsePluginConfig(config: File): ModDoc {
        /*
        {
            name: "dragon example mod",
            packageName: "com.dragonexamplemod",
            modVersion: "1.0.0",
            minMinecraftVersion: "1.7.30",
            maxMinecraftVersion: "*",
            dependency: {
                modName: "other example mod"
                atLeastVersion: "1.0.2",
                atMostVersion: "1.0.05"
            },
            description: "example mod"
        }

         */


        return Json.decodeFromStream(config.inputStream())
    }
}