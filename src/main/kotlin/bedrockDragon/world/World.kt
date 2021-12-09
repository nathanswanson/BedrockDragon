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

package bedrockDragon.world

import bedrockDragon.network.world.WorldInt2
import bedrockDragon.world.chunk.ChunkRelay
import bedrockDragon.world.region.Region
import dev.romainguy.kotlin.math.Float3
import mu.KotlinLogging
import java.util.*

/**
 * [World] holds the region objects and finds [Chunk] or [ChunkRelay] at given coordinates.
 * @author Nathan Swanson
 * @since ALPHA
 */
class World {

    private val loadedChunkRelays = Hashtable<WorldInt2, ChunkRelay>()
    private val loadedRegions = Hashtable<WorldInt2, Region>()
    val logger = KotlinLogging.logger {}

    /**
     * [getOrLoadRelay] will take a position in the world and return the [ChunkRelay] that it is contained in.
     * if it has not been created yet it will make a new one and return that.
     */
    fun getOrLoadRelay(absolutePosition: Float3): ChunkRelay {
        val relayParentRegion = getOrLoadRegion(absolutePosition)
        //getRelayAt is relative
        //mod position xy by 1024
        //int divide position by 64
        return relayParentRegion.getRelayAt((absolutePosition.x.toInt() shr 10) / 64, (absolutePosition.z.toInt() shr 10) / 64)
    }

    /**
     * [getOrLoadRegion] will take a position in the world and return the [Region] that it is contained in.
     * if it has not been created yet it will make a new one and return that.
     */
    fun getOrLoadRegion(absolutePosition: Float3): Region {
        val intPosition = WorldInt2(absolutePosition.x.toInt() shr 10, absolutePosition.z.toInt() shr 10)


        return if (loadedRegions[intPosition] != null) loadedRegions[intPosition]!! else {
            loadedRegions[intPosition] = Region(intPosition.x, intPosition.y)
            loadedRegions[intPosition]!!
        }
    }



    companion object {
        val tempDefault = World()
    }
}
