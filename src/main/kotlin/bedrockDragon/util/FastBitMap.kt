package bedrockDragon.util

import bedrockDragon.world.palette.PaletteSection

class FastBitMap(val size: Int): Cloneable {
    var blockData = IntArray(size)
        private set
    private var paletteResolution = PaletteSection.PaletteResolution.B4


    fun setAt(idx: Int, id: Int) {

        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val offset = bitIndex and 31
        blockData[arrayIndex] =
            blockData[arrayIndex] and (paletteResolution.maxSize shl offset).inv() or (id and paletteResolution.maxSize shl offset)

        if(id != 0)
        {
            println(arrayIndex)
            println(idx)
            println("${blockData[arrayIndex]} ")
            println()
        }

    }

    fun get(idx: Int): Int {
        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val wordOffset = bitIndex and 31
        return blockData[arrayIndex] ushr wordOffset and paletteResolution.maxSize
    }
    //bits are held in int array each int is a bitmask of 32 bits


    override fun clone(): FastBitMap {
        val fastBitMap = FastBitMap(size)
        fastBitMap.blockData = blockData
        fastBitMap.paletteResolution = paletteResolution

        return fastBitMap
    }
}