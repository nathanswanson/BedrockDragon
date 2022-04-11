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
import bedrockDragon.player.Player
import bedrockDragon.registry.DSLBase
import bedrockDragon.registry.Registry
import bedrockDragon.util.WorldInt2
import bedrockDragon.world.chunk.Chunk
import bedrockDragon.world.chunk.ChunkRelay
import bedrockDragon.world.region.AnvilSource
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import mu.KotlinLogging

/**
 * [World] holds the region objects and finds [Chunk] or [ChunkRelay] at given coordinates.
 * @author Nathan Swanson
 * @since ALPHA
 */
class World(val name: String): DSLBase() {

    val worldSource = AnvilSource(this)
    val logger = KotlinLogging.logger {}
    var playerCount = 0

    /**
     * [getOrLoadRelay] will take a position in the world and return the [ChunkRelay] that it is contained in.
     * if it has not been created yet it will make a new one and return that.
     */
    fun getOrLoadRelay(absolutePosition: Float3): ChunkRelay {
        //getRelayAt is relative
        //mod position xy by 1024
        //int divide position by 64
        return worldSource.getRelayAt(((absolutePosition.x.toInt()) shr 6).mod(8), ((absolutePosition.z.toInt()) shr 6).mod(8))
    }

    /**
     * [getOrLoadRelayIdx] will take a position in the world and return its relay.
     * relay(0,0) will be the relay for coord(0,0) to coord(64,64) for example.
     */
    fun getRelayOffset(absolutePosition: Float3, offset: Float2): ChunkRelay {
        return getOrLoadRelay(absolutePosition)
    }


    /**
     * [getChunkAt] takes absolute coordinates and returns the chunk. player coordinates can be inputted and the chunk they are on will be returned.
     */
    fun getChunkAt(position: Float3): Chunk {
        return worldSource.getChunkAtIdx(position.x.toInt() shr 4, position.z.toInt() shr 4)
    }

    //this can be iterated not re calculated
    fun getRangeOfChunks(center: Float3, range: Float2, chunkOffset: Float2 = Float2(0f,0f)): Array<Chunk> {
        val chunks = ArrayList<Chunk>()

        for(x in -range.x.toInt()  .. range.x.toInt()) {
            for(z in -range.y.toInt() .. range.y.toInt()) {
                chunks.add(worldSource.getChunkAtIdx((((center.x).toInt() shr 4) + x) + chunkOffset.x.toInt(), (((center.z).toInt() shr 4) + z) + chunkOffset.y.toInt()))
            }
        }
        return chunks.toTypedArray()
    }

    /**
     * [getBlockAt] will find the block with the given coordinates. If the block is an unloaded chunk it will return air.
     */
    fun getBlockAt(position: Float3): Block {
        //convert player position to relay space
        return getChunkAt(position).getBlockAt(position)
    }


    fun destroyBlock(position: Float3) : Block {
        val removedBlock = getBlockAt(position)

        return removedBlock
    }

    /**
     * [spawnEntity] will spawn an entity at the given coordinates.
     */
    fun spawnEntity(position: Float3, entity: String, tempPlayer: Player): Boolean {
        val entityObject = Registry.ENTITY_REGISTRY[entity]
        entityObject.position = position
        tempPlayer.chunkRelay.addEntity(entityObject)
        return true
    }
}