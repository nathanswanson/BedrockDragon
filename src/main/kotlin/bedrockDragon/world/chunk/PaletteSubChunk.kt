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
import bedrockDragon.util.bitmap.EvenParityBitMap
import bedrockDragon.util.OddParityBitMap
import bedrockDragon.util.extension.writeLInt
import bedrockDragon.world.PaletteGlobal
import dev.romainguy.kotlin.math.Float3
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

/**
 *
 * @author Nathan Swanson
 * @since ALPHA
 */
class PaletteSubChunk(var paletteResolution: PaletteResolution) {

    private val blockCount = 4096 //16 * 16 * 16
    val logger = KotlinLogging.logger {}


    private val palette = ArrayList<Int>()
    private val paletteString = ArrayList<String>()

    private val blockBits = if (paletteResolution.size * paletteResolution.entriesPerWord == 32)
        EvenParityBitMap(getWordsForSize(), paletteResolution) else
            OddParityBitMap(getWordsForSize(), paletteResolution)

    private fun getPaletteHeader(runtime: Boolean): Int {
        return (paletteResolution.size shl 1) or if (runtime) 1 else 0
    }

    fun set(idx: Int, id: Int) {
        blockBits.setAt(idx, global2SectionId(id))
    }

    fun get(idx: Int): Int {
        return blockBits.get(idx)
    }

    fun getBlock(position: Float3): Block {
        if(paletteString.isEmpty())
        {
            return PaletteGlobal.blockRegistry["minecraft:air"]!!
        }
        return PaletteGlobal.blockRegistry[paletteString[get((position.y.mod(16.0) + (position.z.mod(16.0) * 16) + (position.x.mod(16.0) * 16 * 16)).toInt() and 4095)]] ?: PaletteGlobal.blockRegistry["minecraft:bedrock"]!!
    }

    fun debugGetChunk() {
        for(x in 0 until 16) {
            for(y in 0 until 16) {
                for(z in 0 until 16) {
                    if(get(z.mod(16) + (y.mod(16) * 16) + (x.mod(16) * 16 * 16)) == 9) {
                        println("$x:$y:$z")
                    }
                }
            }
        }
    }

    fun getWordsForSize(): Int {
        return blockCount / paletteResolution.entriesPerWord + if (blockCount % paletteResolution.entriesPerWord == 0) 0 else 1
    }

    fun global2SectionId(globalId: Int): Int {
        var idx = palette.indexOf(globalId)
        if(idx != -1)
            return idx

        //id doesn't exist add it
        idx = palette.size
        if(idx > paletteResolution.maxSize) {
           resize()
        }
        palette.add(globalId)
        return idx
    }

    private fun resize() {
        paletteResolution = PaletteResolution.values()[paletteResolution.ordinal+1]
    }


    /**
     * Please note, entriesPerWord is for Int32 NOT Long64 entries would be 2x.
     */
    enum class PaletteResolution(val size: Int,val entriesPerWord: Int) {
        B2(2, 16),
        B4(4,8),
        B5(5, 6),
        B6(6, 5),
        B8(8, 4),
        RAW(15, 4);
        val maxSize = (1 shl size) - 1

    }

    fun encode(outputStream: FastByteArrayOutputStream) {
        outputStream.write(getPaletteHeader(true)) //palette version
        if(palette.isEmpty()) {
            //empty chunk
            palette.add(PaletteGlobal.getRuntimeIdFromName("minecraft:air"))
        }
        blockBits.blockData.forEach {
            outputStream.writeLInt(it) //leInt
        }

        VarInt.writeVarInt(palette.size, outputStream)//palette size
        palette.forEach { VarInt.writeVarInt(it, outputStream) }//palatte as varInts


        //empty palette footer
        outputStream.write(5)
        for(i in 0 until 256) {
            outputStream.writeLInt(0)
        }
        VarInt.writeVarInt(1 , outputStream)
        VarInt.writeVarInt(PaletteGlobal.getRuntimeIdFromName("minecraft:air"), outputStream)
        // this is a second palette used for (I think) water logging.
        // also, can be used to combine two blocks on one position
    }
    //todo use also to avoid memory assignment
    companion object {

        private fun getSmallestUsablePalette(size: Int): PaletteResolution {

            if(size <= 3)
                return PaletteResolution.B2
            if(size <= 15)
                return PaletteResolution.B4
            if(size <= 31)
                return PaletteResolution.B5
            if(size <= 63)
                return PaletteResolution. B6
            if(size <= 255)
                return PaletteResolution.B8 //highest resolution offered

            return PaletteResolution.RAW
        }

        fun parseBlockStateNBT(nbtCompound: NbtCompound): PaletteSubChunk {

            nbtCompound["data"]?.let{ it ->
                val data = it.nbtLongArray
                var unknownPaletteId = false
                val palette = nbtCompound["palette"]!!.nbtList.toList()
                val blockPalette = PaletteSubChunk(getSmallestUsablePalette(palette.size))
                val wordPerLong = ceil(4096.0 / data.size).toInt()  //16
                val wordSize = 64 / wordPerLong //4
                val remainder = floor(64.0 % (wordPerLong * wordSize)).toInt()
                palette.forEach {
                    blockPalette.paletteString.add(it.nbtCompound["Name"]!!.nbtString.value)
                    val entry = PaletteGlobal.getRuntimeIdFromName(it.nbtCompound["Name"]!!.nbtString.value)//double lambda it needs to be specified //todo
                    if (entry != -1) {
                        blockPalette.palette.add(entry)
                    } else {
                        unknownPaletteId = true
                        //println(it.nbtCompound["Name"]!!.nbtString.value)
                        blockPalette.palette.add(PaletteGlobal.getRuntimeIdFromName("minecraft:bedrock"))
                    }

                }
                for(x in 0 until 16) {
                    for(y in 0 until 16) {
                        for(z in 0 until 16) {
                            //todo use bitwise
                            val blockidx = y*16*16 + z*16 + x //range from 0 until 4096
                            val arrayIdx = blockidx / wordPerLong
                            val arrayOffset = (blockidx % wordPerLong) * wordSize
                            /*
                            l: Long = data[arrayIdx]
                             */
                            try {

                                //val block = (data[arrayIdx] ushr (((wordSize * wordPerLong) - wordSize) - arrayOffset)) and ((1 shl wordSize) - 1).toLong()
                                val block = (data[arrayIdx] ushr arrayOffset) and ((1 shl wordSize) - 1).toLong()/*blockPalette.paletteResolution.maxSize.toLong()*/


                                blockPalette.blockBits.setAt((x*16*16) + z*16 + y, block.toInt())
                            } catch (e: ArrayIndexOutOfBoundsException) {
                                println()
                            }

                        }
                    }
                }


               // if(unknownPaletteId)
                   // blockPalette.logger.warn { """Unknown Block was loaded from world file with attempted Palette:
                     //   $palette
                     //   palette runtimeId Attempt: ${blockPalette.palette}
                     //       """ }

                return blockPalette
            }
        return PaletteSubChunk(PaletteResolution.B2) // empty
        }
    }

    fun isEmpty(): Boolean {
        return palette.size <= 1
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for(i in 0 until 4096) {
            builder.append("${blockBits.get(i)} ")
            if((i + 1)% 16 == 0) {
                builder.appendLine()
            }
        }
        builder.append(paletteString)

        return builder.toString()
    }
}
