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

package bedrockDragon.world.chunk

import bedrockDragon.block.Block
import bedrockDragon.network.raknet.VarInt
import bedrockDragon.player.Player
import bedrockDragon.util.ISavable
import bedrockDragon.util.SaveStatus
import bedrockDragon.util.WorldInt2
import bedrockDragon.world.PaletteGlobal
import dev.romainguy.kotlin.math.Float3
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import kotlin.io.path.Path

/**
 * [Chunk], officially a FullChunk or LevelChunk, holds all data for a given 16x16 part of the world.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Chunk(val position: WorldInt2,
            val parent: ChunkRelay?): ISavable  {

    var playersUpToDate = HashSet<Player>()

    var lastUpdate = 0L
    private var inhabitedTime = 0L
    private var isLightOn: Boolean = true
    private var status = "empty" //this isnt actually being used very well TODO
    private var sectionCount = 0

    private var sections = ArrayList<SubChunk>()
    private var fluidTicks = ArrayList<NbtTag>()
    private var postProcessing = ArrayList<NbtTag>()
    private var blockTicks = ArrayList<NbtTag>()
    private lateinit var heightMaps : NbtCompound
    private lateinit var structures : NbtCompound
    private var blockEntities = ArrayList<NbtTag>()

    override val fileName = Path("")
    var loadStatus = SaveStatus.EMPTY

    override fun save(nbtBuilder: NbtCompoundBuilder) {
        nbtBuilder.put("DataVersion", 2230)
        nbtBuilder.put("Level", buildNbtCompound{
            put("xPos", position.x)
            put("zPos", position.y)
            put("isLightOn", isLightOn)
            put("InhabitedTime", inhabitedTime)
            put("LastUpdate", lastUpdate)
            put("Status", status)
            //TileTicks
            //TileEntities
            //Sections
            //PostProcessing
            //LiquidTicks
            //Entities
            //Biomes
            //Structures
            //Heightmaps
        }) //Level

    }

    override fun read() {
        TODO("Not yet implemented")
    }

    /**
     * [encodePayload] converts a chunk into a byte array ready for a packet.
     */
    fun encodePayload(): FastByteArrayOutputStream {
        val stream = FastByteArrayOutputStream(1024)

        //sections
        sections.forEach {
            stream.write(it.encodePayload())
        }


        //biome array
        stream.write(ByteArray(25) {((127 shl 1) or 1).toByte()})
        //border blocks
        stream.write(0)
        //extra data
        VarInt.writeUnsignedVarInt(0, stream)

        return stream
    }

    fun readyNonEmptySectionCount(): Int {
        return sectionCount
    }

    fun updateBlockAt(position: Float3, block: Block)  {
        //x check
        if(position.x >= 16 || position.x < 0)
            throw IllegalArgumentException("position x in $position is either greater that 16 or less then 0.")
        //y check
        if(position.y >= 255 || position.y < -64)
            throw IllegalArgumentException("position y in $position is either greater that 255 or less then -64.")
        //z check
        if(position.z >= 16 || position.z < 0)
            throw IllegalArgumentException("position z in $position is either greater that 16 or less then 0.")

    }

    fun getBlockAt(position: Float3): Block {
        return sections[(position.y.toInt() shr 4) + 4].paletteSubChunk!!.getBlock(position)
    }

    fun encodeNbtToStorage(): ByteArray {
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Zlib // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }
        val builder = buildNbtCompound("") { save(this) }
        return nbt.encodeToByteArray(builder)
    }

    private fun decodeNbtFromStorage(byteArray: ByteArray) {
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Zlib // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }

        val decodedNBT = nbt.decodeFromByteArray<NbtCompound>(byteArray)[""]!!.nbtCompound
        //todo add safety check for null
        //why is there x y and z pos for chunks...
        status = decodedNBT["Status"]?.nbtString?.value ?: "empty"
        position.x = decodedNBT["xPos"]?.nbtInt?.value ?: 0
        //position.y = decodedNBT["yPos"]!!.nbtInt.value.toFloat()
        position.y = decodedNBT["zPos"]?.nbtInt?.value ?: 0
        lastUpdate = decodedNBT["LastUpdate"]?.nbtLong?.value ?: 0L
        inhabitedTime = decodedNBT["InhabitedTime"]?.nbtLong?.value ?: 0L
        isLightOn = decodedNBT["isLightOn"]?.nbtByte?.booleanValue ?: false
        decodedNBT["sections"]!!.nbtList.filter{
            //it.nbtCompound["block_states"]?.nbtCompound?.containsKey("data") == true
            //it.nbtCompound["Y"]?.nbtByte?.value!! in 0..15
            true
        }.map {
            sectionCount++
            SubChunk.decodeFromNbt(it.nbtCompound)
        }.toList().toCollection(sections)
        decodedNBT["block_entities"]!!.nbtList.toCollection(blockEntities)
        heightMaps = decodedNBT["Heightmaps"]!!.nbtCompound
        decodedNBT["block_ticks"]!!.nbtList.toCollection(blockTicks)
        structures = decodedNBT["structures"]!!.nbtCompound
        decodedNBT["fluid_ticks"]!!.nbtList.toCollection(fluidTicks)
        decodedNBT["PostProcessing"]!!.nbtList.toCollection(postProcessing)

    }

    fun initChunkFromStorage() {
        if(loadStatus == SaveStatus.EMPTY) {
            val data = parent!!.region.readChunkBinary(this)
            decodeNbtFromStorage(data)
            loadStatus = SaveStatus.LOADED
        }
    }

    override fun toString(): String {
        return """Chunk: 
            |pos: $position
            |region: $parent""".trimMargin()
    }

    private class SubChunk {
        lateinit var blockStates : NbtCompound
        lateinit var biomes : NbtCompound
        lateinit var blockLight : NbtByteArray

        var paletteSubChunk: PaletteSubChunk? = null
        var y: Byte = 0 //signed
        fun encodePayload(): ByteArray {
            val stream = FastByteArrayOutputStream(128)

            if(paletteSubChunk != null) {
                stream.write(8)
                stream.write(2)
                paletteSubChunk?.encode(stream)
               // stream.write(PaletteSubChunk.emptyPaletteFooter)
            }

            //storage
            stream.trim()
            return stream.array.copyOfRange(0, stream.length)
        }

        companion object {
            fun decodeFromNbt(data: NbtCompound): SubChunk {
                val subChunk = SubChunk()

                subChunk.blockStates = data["block_states"]!!.nbtCompound
                subChunk.paletteSubChunk = PaletteSubChunk.parseBlockStateNBT(subChunk.blockStates)
                subChunk.biomes = data["biomes"]!!.nbtCompound
                subChunk.blockLight = data["BlockLight"]?.nbtByteArray ?: NbtByteArray(ByteArray(0))
                subChunk.y = data["Y"]!!.nbtByte.value

                return subChunk
            }
        }
    }
}


