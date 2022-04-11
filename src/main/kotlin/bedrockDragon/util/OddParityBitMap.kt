package bedrockDragon.util

import bedrockDragon.util.bitmap.BitMap
import bedrockDragon.world.chunk.PaletteSubChunk

class OddParityBitMap(size: Int, paletteResolution: PaletteSubChunk.PaletteResolution) : BitMap(size, paletteResolution) {


    /**
     * The setAt() method handles the proper positioning of bits; the idx input should be the block position in
     * the backing array.
     *
     */
    override fun setAt(idx: Int, id: Int) {
        //idx out of bounds
        if(idx * paletteResolution.size shr 5 >= blockData.size)
            throw IndexOutOfBoundsException("The index provided exceeds the length of words in the Bit Map")
        //int is larger than palette bit size
        if(id > paletteResolution.maxSize)
            throw IllegalArgumentException("The palette cannot handle an id of $id with a current max bit size of ${paletteResolution.maxSize}")
        //for B5 (size 5, wordCount 6)
        //idx 6, 0b11 (3)
        //bitIndex =  6 * 5 = 30
        //arrayIndex = 1
        //offset = 30 and 29 = 30
        val arrayIndex = idx / paletteResolution.entriesPerWord
        val offset = (idx % paletteResolution.entriesPerWord) * paletteResolution.size
        blockData[arrayIndex] =
            blockData[arrayIndex] and (paletteResolution.maxSize shl offset).inv() or (id and paletteResolution.maxSize shl offset)
    }

    override fun get(idx: Int): Int {

        val arrayIndex = idx / paletteResolution.entriesPerWord
        val wordOffset = (idx % paletteResolution.entriesPerWord) * paletteResolution.size
        return (blockData[arrayIndex] ushr wordOffset) and paletteResolution.maxSize
    }
}