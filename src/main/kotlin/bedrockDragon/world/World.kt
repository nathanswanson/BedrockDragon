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

import bedrockDragon.block.Block
import bedrockDragon.entity.Entity
import bedrockDragon.network.world.WorldInt2
import bedrockDragon.world.chunk.Chunk
import bedrockDragon.world.chunk.ChunkRelay
import bedrockDragon.world.region.Region
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.pow
import mu.KotlinLogging
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * [World] holds the region objects and finds [Chunk] or [ChunkRelay] at given coordinates.
 * @author Nathan Swanson
 * @since ALPHA
 */
class World {

    private val loadedRegions = HashMap<WorldInt2, Region>()
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
        return relayParentRegion.getRelayAt(((absolutePosition.x.toInt()) shr 6).mod(8), ((absolutePosition.z.toInt()) shr 6).mod(8))
    }

    /**
     * [getOrLoadRegion] will take a position in the world and return the [Region] that it is contained in.
     * if it has not been created yet it will make a new one and return that.
     */
    private fun getOrLoadRegion(absolutePosition: Float3): Region {
        val intPosition = WorldInt2(absolutePosition.x.toInt() shr 10, absolutePosition.z.toInt() shr 10)


        return if (loadedRegions[intPosition] != null) loadedRegions[intPosition]!! else {
            loadedRegions[intPosition] = Region(intPosition.x, intPosition.y, this)
            loadedRegions[intPosition]!!
        }
    }

    fun getOrLoadRelayIdx(intPosition: WorldInt2): ChunkRelay {
        val relayParentRegion = getOrLoadRegionIdx(WorldInt2(intPosition.x shr 4, intPosition.y shr 4))
        return relayParentRegion.getRelayAt(intPosition.x.mod(8),intPosition.y.mod(8))
    }

    private fun getOrLoadRegionIdx(intPosition: WorldInt2): Region {
        return if (loadedRegions[intPosition] != null) loadedRegions[intPosition]!! else {
            loadedRegions[intPosition] = Region(intPosition.x, intPosition.y, this)
            loadedRegions[intPosition]!!
        }
    }

    fun getChunkAt(position: Float3): Chunk {
        return getOrLoadRelay(position).getChunk2D(
            (position.x.toInt() shr 4).mod(4),
            (position.z.toInt() shr 4).mod(4)
        )
    }

    fun getBlockAt(position: Float3): Block {
        //convert player position to relay space
        return getChunkAt(position).getBlockAt(position)
    }

    fun spawnEntity(position: Float3, entity: Entity): Boolean {
        return false
    }

    companion object {
        val tempDefault = World()
    }
}