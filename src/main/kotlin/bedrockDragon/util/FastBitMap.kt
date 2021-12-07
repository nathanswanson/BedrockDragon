package bedrockDragon.util

class FastBitMap(initialSize: Int): Cloneable {
    private var blockData = ArrayList<Int>()

    private var paletteResolution = PaletteResolution.B4


    fun setAt(idx: Int, id: Int) {
        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val offset = bitIndex and 31

        blockData[arrayIndex] = blockData[arrayIndex] and (paletteResolution.maxSize shl offset).inv() or (id and paletteResolution.maxSize) shl offset
    }

    fun get(idx: Int): Int {
        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val wordOffset = bitIndex and 31
        return blockData[arrayIndex] ushr wordOffset and paletteResolution.maxSize
    }
    //bits are held in int array each int is a bitmask of 32 bits

    private enum class PaletteResolution(val size: Int,val entriesPerWord: Int) {
        B4(4,8),
        B5(5, 6),
        B6(6, 5),
        B8(8, 4);

        val maxSize = 1 shl size - 1

    }

    override fun clone(): FastBitMap {
        val fastBitMap = FastBitMap(0)
        fastBitMap.blockData = blockData
        fastBitMap.paletteResolution = paletteResolution

        return fastBitMap
    }
}