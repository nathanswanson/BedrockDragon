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

package bedrockDragon.world.chunk

import bedrockDragon.entity.Entity
import bedrockDragon.player.Player
import bedrockDragon.reactive.MovePlayer
import bedrockDragon.reactive.ReactivePacket
import bedrockDragon.util.WorldInt2
import bedrockDragon.world.region.Region
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.truncate

/**
 * [ChunkRelay] is not a minecraft concept, This is a grouping much like a [Region], that
 * shares a common semi-mutable state. ChunkRelay's act as an information event handler.
 * @author Nathan Swanson
 * @since ALPHA
 */
class ChunkRelay(val x: Int,val z: Int,val region: Region) {
    constructor(float2: WorldInt2, region: Region) : this(float2.x, float2.y, region)



    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val logger = KotlinLogging.logger {}
    private val subscriptionSharedFlow = MutableSharedFlow<ReactivePacket<*>>()
    private val nonMutableFlow = subscriptionSharedFlow.asSharedFlow()
    private val playerLastPublishPosition = ConcurrentHashMap<Player, WorldInt2>()
    private val jobs = HashMap<Player, Job>()
    //chunks are a 4x4 grid laid in an array and are absolute position in world
    // x = idx / 4  0: 0,1,2,3  1: 4,5,6,7 ...
    // y = idx mod 4     0: 0,4,8,12 1: 1,5,9,13...

    val chunks = Array(16) { i ->
        Chunk(
            WorldInt2(
                ((i shr 2) + (getWorldCoordinates().x shl 2)),
                ((i and 3) + (getWorldCoordinates().y shl 2))
            ),
            this
        )
    }

    fun addEntity(entity: Entity) {

    }
    /**
     * [addPlayer] will subscribe a new player to this relay so when
     * anything happens we notify this new player as well.
     */
    fun addPlayer(player: Player) {
        playerLastPublishPosition[player] = getChunkAbsolute(player)
        player.updateChunkPublisherPosition()
        sendAllChunksForPlayer(player)

        jobs[player] = scope.launch {
            nonMutableFlow.filter { true }
                .collectLatest {
                    if(it is MovePlayer) {
                        checkChunkNeeds(it.sender as Player)
                    }
                    player.emitReactiveCommand(it)
                }
        }
    }

    private fun sendAllChunksForPlayer(player: Player) {


        val offsetInRelayX = (player.position.x.toInt() shr 4) and 3
        val offsetInRelayz = (player.position.z.toInt() shr 4) and 3

        for(x in -player.renderDistance until player.renderDistance + 1) {
            for(z in -player.renderDistance until player.renderDistance + 1) {
                val relayProvider = region.world.getOrLoadRelayIdx(WorldInt2(
                    this.x + (x + offsetInRelayX).floorDiv(4),
                    this.z + (z + offsetInRelayz).floorDiv(4),
                ))
                player.sendChunk(relayProvider.getChunk2D((x + offsetInRelayX).mod(4),(z + offsetInRelayz).mod(4)))
            }
        }
//        var stringB = StringBuilder()
//
//        for(x in -10..10) {
//            for(z in -10..10) {
//                if(player.sendChunkCoord.contains(WorldInt2(x,z))) {
//                    stringB.append("x")
//                } else {
//                    stringB.append("-")
//                }
//            }
//            stringB.append("\n")
//        }
//        println(stringB)
//        println(player.sendChunkCoord.size)
    }

    private fun addPlayerFromAdjacentRelay(player: Player) {
        playerLastPublishPosition[player] = getChunkAbsolute(player)
        player.updateChunkPublisherPosition()
        player.chunkRelay = this

        jobs[player] = scope.launch {
            nonMutableFlow.filter { true }
                .collectLatest {
                    player.emitReactiveCommand(it)
                    if(it is MovePlayer) {
                        checkChunkNeeds(it.sender as Player)
                    }
                }
        }
    }
    private fun getChunkAbsolute(player: Player): WorldInt2 {
        return WorldInt2(player.position.x.toInt() shr 4, player.position.z.toInt() shr 4)
    }
    private fun checkChunkNeeds(player: Player) {

        val worldPosition = getWorldCoordinates()
        val xOffset = worldPosition.x shl 6
        val zOffset = worldPosition.y shl 6

        val currentChunkPos = getChunkAbsolute(player)
        val deltaMovement = WorldInt2(currentChunkPos.x - (playerLastPublishPosition[player]?.x ?: 0), currentChunkPos.y - (playerLastPublishPosition[player]?.y ?: 0) )
        //check if entered new chunk
        if(deltaMovement != WorldInt2(0,0)) {
            //check if entered new region

            sendDeltaChunksForPlayer(player, deltaMovement.x, deltaMovement.y)

            if(!(player.position.x.toInt() in xOffset..xOffset + 64 && player.position.z.toInt() in zOffset..zOffset + 64 )) {
                //transfer player to new relay
                //get relay adjacent
                val newRelayPosition = WorldInt2(player.position.x.toInt() shr 6, player.position.z.toInt() shr 6)
                passToNewRelay(player, region.world.getOrLoadRelayIdx(newRelayPosition))
            }

            playerLastPublishPosition[player] = getChunkAbsolute(player)
            player.updateChunkPublisherPosition()
        }
    }

    private fun sendDeltaChunksForPlayer(player: Player, x: Int, z: Int) {
        val deltaChunks = ArrayList<WorldInt2>()
        //todo efficency
        val viewDistanceToRelay = (player.renderDistance + 3 and 0x03.inv()) shr 2
        val relayOffsetX = truncate((player.position.x / 16.0).mod(4f)).toInt()
        val relayOffsetZ = truncate((player.position.z / 16.0).mod(4f)).toInt()
            if(x != 0) {
            for(eastWest in -player.renderDistance until player.renderDistance + 1) {

                val step = sign(x.toDouble()).toInt()
                val playerChunkPos = getChunkAbsolute(player)

                val relay = region.world.getOrLoadRelayIdx(WorldInt2((player.position.x.toInt() shr 6)  + (viewDistanceToRelay * step), (eastWest + playerChunkPos.y).floorDiv(4)))
                val chunkGrabbed = relay.getChunk2D((relayOffsetX), (relayOffsetZ + eastWest).mod(4))

                player.sendChunk(chunkGrabbed)
                deltaChunks.add(chunkGrabbed.position)

            }
        }
        if(z != 0) {
            for(northSouth in -player.renderDistance until player.renderDistance + 1) {
                val step = sign(z.toDouble()).toInt()
                val playerChunkPos = getChunkAbsolute(player)
                val relay = region.world.getOrLoadRelayIdx(WorldInt2((northSouth + playerChunkPos.x).floorDiv(4),(player.position.z.toInt() shr 6)  + (viewDistanceToRelay * step)))
                val chunkGrabbed = relay.getChunk2D((relayOffsetX + northSouth).mod(4), (relayOffsetZ))

                player.sendChunk(chunkGrabbed)
                deltaChunks.add(chunkGrabbed.position)
            }
        }
        var stringB = StringBuilder()
        for(x in 10 downTo -10) {
            if((x + 1)% 4 == 0)
                stringB.appendLine()
            for(z in -10..10) {
                if(z % 4 == 0)
                    stringB.append(" ")
                if(x == 0 && z == 0) {
                    stringB.append("C")
                    continue
                }
                if(player.sendChunkCoord.contains(WorldInt2(x,z))) {
                    if(deltaChunks.contains(WorldInt2(x,z))) {
                        stringB.append("O")
                    } else {
                        stringB.append("x")
                    }
                } else if(deltaChunks.contains(WorldInt2(x,z))) {
                    stringB.append("D")
                } else {
                    stringB.append(" ")
                }
            }
            stringB.append("\n")
        }

        println(stringB)
    }

    private infix fun Int.toward(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to - step, step)
    }

    private fun getWorldCoordinates(): WorldInt2 {
        return WorldInt2(
            ((region.x shl 4) + x) + if(region.x < 0) 8 else 0,
            ((region.z shl 4) + z) + if(region.z < 0) 8 else 0
        )}

    /**
     * [invoke] either the chunk or another subscriber can use this method to broadcast information
     * to every subscriber.
     */
    fun invoke(reactivePacket: ReactivePacket<*>) {
        scope.launch {
            subscriptionSharedFlow.emit(reactivePacket)
        }
    }

    /**
     * [removePlayer] will unsubscribe a player from a relay.
     */
    fun removePlayer(player: Player) {
        jobs[player]?.cancel()
        jobs.remove(player)
    }

    /**
     * [passToNewRelay] allows fast transfer of a player from one relay to another.
     */
    private fun passToNewRelay(player: Player, relay: ChunkRelay) {
        removePlayer(player)
        relay.addPlayerFromAdjacentRelay(player)
    }

    fun getChunk2D(x: Int, z: Int) : Chunk {
        return chunks[(x shl 2) + z]
    }

    override fun toString(): String {
        return "ChunkRelay: x:$x z:$z in Region: $region"
    }
}