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

package bedrockDragon.world.region

import java.io.InputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.inputStream

/**
 * RegionManifest decodes the header of a given region(.mca) file to determine the location of
 * each chunk and to determine gaps of memory that can be filled to avoid fragmentation.
 * @author Nathan Swanson
 * @since ALPHA
 */
class RegionManifest(val region: Path) {
    private val manifest = LinkedList<UnallocatedSection>() //todo add lazy modifier
    private var lastSlotPointer = 0

    init {
        readManifest() //see above todo
    }

    /**
     * Function is used on first access to parse the header, and used whenever the
     * manifest is dirty and needs to be updated.
     */
    fun readManifest() { //O(n)

        region.inputStream().use {
            var size = 0
            var start = 0
            for(i in 1..1024) {

                //then it exists
                if(readTryte(it) == 0 && it.read() == 0) {
                    if(size == 0) {
                        start = i
                    }
                    size++
                } else if (size != 0) {
                    manifest.add(UnallocatedSection(start, size))
                    size = 0
                }

                if(i == 1023) {
                    manifest.add(UnallocatedSection(start, size))
                }
            }
        }

    }

    /**
     * returns and allocates the first memory address that can hold a given size of blocks.
     */
    fun getFirstUsableSlot(size: Int): Int {

        var lastIndex = 0

        val section = manifest.firstOrNull() {
            lastIndex = it.end
            it.take(size)
        }
            ?: return lastIndex //null if none found

        return section.start-size



    }

    //This is going to be some work. when a chunk moves from getting to big this method should
    //open up its old memory space for other chunks. TODO
    fun unAllocate(start: Int, end: Int) {

    }

    /**
     * readTryte decodes a 3 byte integer from a given stream.
     */
    fun readTryte(inputStream: InputStream): Int {

        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()

    }

    /**
     * UnallocatedSection is a very basic object that holds start and end positions of memory addresses and
     * allows the removal of those memory positions.
     * @author Nathan Swanson
     * @since ALPHA
     */
    private class UnallocatedSection(var start: Int, var end: Int) {

        fun take(size: Int): Boolean {
            if(start + size > this.end)
                return false

            start+=size
            return true
        }

    }
}