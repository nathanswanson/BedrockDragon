package bedrockDragon.world.palette

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.util.FastBitMap
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import sun.jvm.hotspot.utilities.BitMap
import java.util.*
import kotlin.collections.ArrayList

class PaletteSection {

    val blockCount = 4096 //16 * 16 * 16
    private var paletteResolution = PaletteResolution.B4

    private val palette = ArrayList<Int>()
    val blockBits = FastBitMap(16)

    private fun getPaletteHeader(runtime: Boolean): Int {
        return (paletteResolution.size shl 1) or if (runtime) 1 else 0
    }

    fun getWordsForSize(size: Int): Int {
        return size / paletteResolution.entriesPerWord + if (size % paletteResolution.entriesPerWord == 0) 0 else 1
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

    private enum class PaletteResolution(val size: Int,val entriesPerWord: Int) {
        B4(4,8),
        B5(5, 6),
        B6(6, 5),
        B8(8, 4);

        val maxSize = 1 shl size - 1

    }

    fun encode(outputStream: FastByteArrayOutputStream) {
        outputStream.write(getPaletteHeader(true)) //palette version

        for(i in 1..blockCount) {
            VarInt.writeVarInt(blockBits.get(i), outputStream)
        }

        VarInt.writeVarInt(palette.size, outputStream)//palette size

        palette.forEach { VarInt.writeVarInt(it, outputStream) }//palatte as varInts
    }
}