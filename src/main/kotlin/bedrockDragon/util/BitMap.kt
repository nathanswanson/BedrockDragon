package bedrockDragon.util

import bedrockDragon.world.chunk.PaletteSubChunk

abstract class BitMap(val size: Int, var paletteResolution: PaletteSubChunk.PaletteResolution) {
    var blockData = IntArray(size)
        private set

    abstract fun setAt(idx: Int, id: Int)
    abstract fun get(idx: Int): Int

    override fun toString(): String {
        val builder = StringBuilder()

        for(i in 0 until 4096) {
            builder.append("${get(i)} ")
        }

        return builder.toString()
    }
}