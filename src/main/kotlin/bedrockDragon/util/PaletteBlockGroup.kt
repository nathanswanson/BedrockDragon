package bedrockDragon.util

import bedrockDragon.network.raknet.VarInt
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.BitEncoding

class PaletteBlockGroup {

    val blockCount = 4096 //16 * 16 * 16
    private var paletteResolution = PaletteResolution.B4

    val palette = ArrayList<Int>() //16
    val blockBits = BitSet()

    fun getPaletteHeader(runtime: Boolean): Int {
        return (paletteResolution.size shl 1) or if (runtime) 1 else 0
    }

    fun getWordsForSize(size: Int): Int {
        return size / paletteResolution.entriesPerWord + if (size % paletteResolution.entriesPerWord == 0) 0 else 1
    }

    enum class PaletteResolution(val size: Int,val entriesPerWord: Int) {
        B4(4,8),
        B5(5, 6),
        B6(6, 5),
        B8(8, 4)

    }

    fun encode(outputStream: FastByteArrayOutputStream) {
        outputStream.write(getPaletteHeader(true)) //palette version

        //words

        VarInt.writeVarInt(palette.size, outputStream)//palette size

        palette.forEach { VarInt.writeVarInt(it, outputStream) }//palatte as varInts
    }
}