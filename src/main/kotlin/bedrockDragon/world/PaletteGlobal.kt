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

package bedrockDragon.world

import bedrockDragon.block.Block
import bedrockDragon.registry.VanillaBlocks
import bedrockDragon.item.Item
import bedrockDragon.registry.VanillaItems
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import java.util.HashMap

/**
 * PaletteGlobal parses [resources/blocks.json] (A file generated from a vanilla minecraft server).
 * The parsed information contains a mapping of (name,id).
 * @author Nathan Swanson
 * @since ALPHA
 */
@OptIn(ExperimentalSerializationApi::class)
object PaletteGlobal {
    val globalBlockPalette = HashMap<String, ArrayList<PaletteEntry>>()
    val blockRegistry = HashMap<String, Block>() //contract needed for private access todo
    val itemRegistry = HashMap<String, Item>()

    init {
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.None // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }

        //Create Minecraft Block Objects.
        VanillaBlocks
        //create Minecraft Item Objects
        VanillaItems

        val runtime = ClassLoader.getSystemResourceAsStream("runtime_block_states.dat")
        val nbtData = nbt.decodeFromStream<NbtCompound>(runtime!!)[""]!!.nbtList
        nbtData.forEach {
            val entryCompound = it.nbtCompound
            globalBlockPalette.putIfAbsent(it.nbtCompound["name"]!!.nbtString.value, ArrayList())

            val paletteEntry = PaletteEntry(entryCompound["name"]!!.nbtString.value,
                entryCompound["version"]!!.nbtInt.value,
                entryCompound["states"]!!.nbtCompound,
                entryCompound["id"]!!.nbtInt.value,
                entryCompound["data"]!!.nbtShort.value,
                entryCompound["runtimeId"]!!.nbtInt.value
                )

            globalBlockPalette[it.nbtCompound["name"]!!.nbtString.value]!!.add(paletteEntry)
        }

        //temporary until I generate own world files
        //Aliases Java -> Bedrock
        globalBlockPalette["minecraft:grass_block"] = globalBlockPalette["minecraft:grass"]!!
        globalBlockPalette["minecraft:grass"] = globalBlockPalette["minecraft:tallgrass"]!!
        globalBlockPalette["minecraft:cave_air"] = globalBlockPalette["minecraft:air"]!!
        globalBlockPalette["minecraft:dead_bush"] = globalBlockPalette["minecraft:deadbush"]!!

        globalBlockPalette["minecraft:polished_andesite"] = getEntryFromName("minecraft:stone", 6)
        globalBlockPalette["minecraft:dirt_path"] = globalBlockPalette["minecraft:grass_path"]!!
        globalBlockPalette["minecraft:white_bed"] = getEntryFromName("minecraft:bed", 0)
        globalBlockPalette["minecraft:oak_trapdoor"] = globalBlockPalette["minecraft:trapdoor"]!!
        //todo side of log
        globalBlockPalette["minecraft:wall_torch"] = globalBlockPalette["minecraft:torch"]!!
        globalBlockPalette["minecraft:beetroots"] = globalBlockPalette["minecraft:beetroot"]!!
        globalBlockPalette["minecraft:magma_block"] = globalBlockPalette["minecraft:magma"]!!
        globalBlockPalette["minecraft:chiseled_quartz_block"] = getEntryFromName("minecraft:quartz_block", 1)
        globalBlockPalette["minecraft:quartz_pillar"] = getEntryFromName("minecraft:quartz_block", 3)
        globalBlockPalette["minecraft:bricks"] = globalBlockPalette["minecraft:brick_block"]!!
        globalBlockPalette["minecraft:sea_lantern"] = globalBlockPalette["minecraft:seaLantern"]!!

        //leaves

        //flora
        globalBlockPalette["minecraft:blue_orchid"] = getEntryFromName("minecraft:red_flower", 1)
        globalBlockPalette["minecraft:dandelion"] = globalBlockPalette["minecraft:yellow_flower"]!!
        globalBlockPalette["minecraft:poppy"] = globalBlockPalette["minecraft:red_flower"]!!

        val jsonData = Json.decodeFromStream<JsonObject>(ClassLoader.getSystemResourceAsStream("blockAlias.json")!!)
        jsonData.forEach {
            it.value.jsonArray.forEachIndexed { idx, blockEntry ->
                globalBlockPalette[blockEntry.jsonPrimitive.content] = getEntryFromName("minecraft:${it.key}", idx)
            }
        }

    }

    fun getRuntimeIdFromName(name: String, data: Int = 0): Int {
        return globalBlockPalette[name]?.get(data)?.runtimeId ?: -1
    }

    fun getAItem(name: String): Item {
        return itemRegistry[name]!!.copy()
        //todo null safety
    }

    private fun getEntryFromName(name: String, data: Int = 0): ArrayList<PaletteEntry> {
        return arrayListOf(globalBlockPalette[name]?.get(data) ?: globalBlockPalette["minecraft:bedrock"]!![0])
    }

    @Serializable
    data class PaletteEntry(val name: String, val version: Int, val states: NbtCompound, val id: Int, val data: Short, val runtimeId: Int)

}