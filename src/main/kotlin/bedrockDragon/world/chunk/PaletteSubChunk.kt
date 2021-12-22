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

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.util.FastBitMap
import bedrockDragon.util.extension.writeLInt
import bedrockDragon.world.PaletteGlobal
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

/**
 *
 * @author Nathan Swanson
 * @since ALPHA
 */
class PaletteSubChunk(var paletteResolution: PaletteResolution) {

    private val blockCount = 4096 //16 * 16 * 16
    val logger = KotlinLogging.logger {}


    private var palette = ArrayList<Int>()
    private val blockBits = FastBitMap(getWordsForSize(), paletteResolution)

    private fun getPaletteHeader(runtime: Boolean): Int {
        return (paletteResolution.size shl 1) or if (runtime) 1 else 0
    }

    fun set(idx: Int, id: Int) {
        blockBits.setAt(idx, global2SectionId(id))
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

    enum class PaletteResolution(val size: Int,val entriesPerWord: Int) {
        B2(2, 16),
        B4(4,8),
        B5(5, 6),
        B6(6, 5),
        B8(8, 4);

        val maxSize = (1 shl size) - 1

    }

    fun encode(outputStream: FastByteArrayOutputStream) {
        outputStream.write(getPaletteHeader(true)) //palette version

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
        VarInt.writeVarInt(134, outputStream)
    }
    //todo use also to avoid memory assignment
    companion object {

        private fun getSmallestUsablePallete(size: Int): PaletteResolution {

            if(size <= 3)
                return PaletteResolution.B2
            if(size <= 14)
                return PaletteResolution.B4

            return PaletteResolution.B8 //highest resolution offered
        }

        fun parseBlockStateNBT(nbtCompound: NbtCompound): PaletteSubChunk {

            nbtCompound["data"]?.let{ it ->
                val data = it.nbtLongArray

                var unknownPaletteId = false
                val palette = nbtCompound["palette"]!!.nbtList.toList()
                val blockPalette = PaletteSubChunk(getSmallestUsablePallete(palette.size))

                val wordPerLong = 4096 / data.size  //16
                val wordSize = 64 / wordPerLong //4

                for(chunkColumn in 0 until 256) {
                    for(y in 0 until 16) {
                        //example assignment as B4

                        val blockidx = chunkColumn + (y * 256) //range from 0 until 4096
                        val arrayIdx = blockidx / (64 / wordSize)
                        val arrayOffset = blockidx % (64 / wordSize) * wordSize

                        //val block = (data[arrayIdx] ushr (4 * arrayOffset)) and 15
                        /*
                        l: Long = data[arrayIdx]
                        mask = ~(1 << wordSize) ex. 1 << 5 = 100000, ~(100000) = 11111
                         */
                        val block = (data[arrayIdx] ushr ((64 - wordSize) - arrayOffset)) and ((1 shl wordSize) - 1).toLong()
                        blockPalette.blockBits.setAt(chunkColumn * 16 + y, block.toInt())
                    }
                }
                palette.forEach {
                    PaletteGlobal.globalBlockPalette[it.nbtCompound["Name"]!!.nbtString.value].let { it1 -> //double lambda it needs to be specified //todo
                        if (it1 != null) {
                            blockPalette.palette.add(it1)
                        } else {
                            unknownPaletteId = true
                            blockPalette.palette.add(134)
                        }
                    }
                }

                if(unknownPaletteId)
                    blockPalette.logger.warn { """Unknown Block was loaded from world file with attempted Palette:
                        $palette
                        palette runtimeId Attempt: ${blockPalette.palette}
                            """ }

                //determine resolution todo
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
        for(i in 1..512) {
            builder.append("${blockBits.get(i)} ")
            if(i % 16 == 0) {
                builder.appendLine()
            }
        }

        return builder.toString()
    }
}
