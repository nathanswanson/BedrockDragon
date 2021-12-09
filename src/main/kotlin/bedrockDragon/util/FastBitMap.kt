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

package bedrockDragon.util

import bedrockDragon.world.data.palette.PaletteSection

/**
 * FastBitMap is specifically designed for use in chunk palette creation. Normally, a byte array is used
 * to send data to client however with the nature of varying lengths of bits its easier to send a bit array
 * instead.
 * @author Nathan Swanson
 * @since ALPHA
 */
class FastBitMap(val size: Int): Cloneable {
    var blockData = IntArray(size)
        private set
    private var paletteResolution = PaletteSection.PaletteResolution.B4


    /**
     * The setAt() method handles the proper positioning of bits; the idx input should be the block position in
     * the backing array.
     *
     */
    fun setAt(idx: Int, id: Int) {
        //idx out of bounds
        if(idx * paletteResolution.size shr 5 >= blockData.size)
            throw IndexOutOfBoundsException("The index provided exceeds the length of words in the Bit Map")
        //int is larger than palette bit size
        if(id > 1 shl paletteResolution.maxSize)
            throw IllegalArgumentException("The palette cannot handle an id of $id with a current max bit size of ${paletteResolution.maxSize}")

        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val offset = bitIndex and 31
        blockData[arrayIndex] =
            blockData[arrayIndex] and (paletteResolution.maxSize shl offset).inv() or (id and paletteResolution.maxSize shl offset)

    }

    /**
     * If this method is being used for the palette assume that the idx range is 1..4096. 4096 represents
     * the 16x16x16 subchunk blocks IDs.
     */
    fun get(idx: Int): Int {
        //idx out of bounds
        if(idx * paletteResolution.size shr 5 >= blockData.size)
            throw IndexOutOfBoundsException("The index provided exceeds the length of words in the Bit Map")

        val bitIndex: Int = idx * paletteResolution.size
        val arrayIndex = bitIndex shr 5
        val wordOffset = bitIndex and 31
        return blockData[arrayIndex] ushr wordOffset and paletteResolution.maxSize
    }


    override fun clone(): FastBitMap {
        val fastBitMap = FastBitMap(size)
        fastBitMap.blockData = blockData
        fastBitMap.paletteResolution = paletteResolution

        return fastBitMap
    }
}