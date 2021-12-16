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
import net.benwoodworth.knbt.*
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author Nathan Swanosn
 * @since ALPHA
 */
class PaletteSubChunk {

    private val blockCount = 4096 //16 * 16 * 16
    private var paletteResolution = PaletteResolution.B2

    private var palette = ArrayList<Int>()
    private val blockBits = FastBitMap(getWordsForSize())

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
        outputStream.write(getPaletteHeader(true))
        for(i in 0 until 256) {
            outputStream.writeLInt(0)
        }
        VarInt.writeVarInt(1 , outputStream)
        VarInt.writeVarInt(134, outputStream)
    }
    //todo use also to avoid memory assignment
    companion object {

        fun parseBlockStateNBT(nbtCompound: NbtCompound): PaletteSubChunk {

            nbtCompound["data"]?.let{ it ->
                val data = it.nbtLongArray

                val blockPalette = PaletteSubChunk()
                blockPalette.paletteResolution = PaletteResolution.B2 //todo
                var idx: Int = 0
                for(chunkColumn in 0 until 256) {
                    for(y in 0 until 16) {
                        //example assignment as B4
                        val wordPerLong = 4096 / data.size  //16
                        val wordSize = 64 / wordPerLong //4
                        val blockidx = chunkColumn + (y * 256) //range from 0 until 4096
                        val arrayIdx = blockidx / wordPerLong
                        val arrayOffset = blockidx % wordPerLong
                        val block = (data[arrayIdx] ushr (4 * arrayOffset)) and 15
                        blockPalette.blockBits.setAt(chunkColumn * 16 + y, block.toInt())
                    }
                }

                val palette = nbtCompound["palette"]!!.nbtList.toList()
                palette.forEach {
                    PaletteGlobal.globalBlockPalette[it.nbtCompound["Name"]!!.nbtString.value]?.let { it1 -> //double lambda it needs to be specified //todo
                        blockPalette.palette.add(it1)
                    }
                }
//                blockPalette.palette.add(234)
//                blockPalette.palette.add(4484)
//                blockPalette.palette.add(4997)
//                blockPalette.palette.add(134)

                //determine resolution todo
                return blockPalette
            }
        return PaletteSubChunk()
        }
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
