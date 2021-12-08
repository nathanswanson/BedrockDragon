package bedrockDragon.world.palette

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.util.FastBitMap
import bedrockDragon.util.writeLInt
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.nbtList
import net.benwoodworth.knbt.nbtLongArray
import java.util.*
import kotlin.collections.ArrayList

class PaletteSection {

    private val blockCount = 4096 //16 * 16 * 16
    private var paletteResolution = PaletteResolution.B4

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
    }

    companion object {
        fun parseBlockStateNBT(nbtCompound: NbtCompound): PaletteSection {
            val data = nbtCompound["data"]!!.nbtLongArray
            val blockPalette = PaletteSection()
            var idx: Int = 0
            data.forEach {
               for(i in 60 downTo 0 step 4) {
                  // print("${it ushr i and 15},")
                   blockPalette.blockBits.setAt(idx, (it ushr i and 15).toInt())
                   idx++

               }
                //println(it)

            }

            val palette = nbtCompound["palette"]!!.nbtList.toList()
            //blockPalette.palette = palette.toCollection()



            //determine resolution todo
            println(blockPalette)
            return blockPalette
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