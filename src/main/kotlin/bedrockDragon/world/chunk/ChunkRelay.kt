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

import bedrockDragon.network.world.WorldInt2
import bedrockDragon.player.Player
import bedrockDragon.reactive.ReactivePacket
import bedrockDragon.world.region.Region
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import mu.KotlinLogging

/**
 * [ChunkRelay] is not a minecraft concept, This is a grouping much like a [Region], that
 * shares a common semi-mutable state. ChunkRelay's act as an information event handler.
 * @author Nathan Swanson
 * @since ALPHA
 */
class ChunkRelay(val x: Int,val z: Int,val parent: Region?) {
    constructor(float2: WorldInt2, region: Region) : this(float2.x.toInt(), float2.y.toInt(), region)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val logger = KotlinLogging.logger {}
    //private val subscribers = HashSet<ISubscriber>() //TODO not thread safe very bad and wont work with more then one relay
    private val subscriptionSharedFlow = MutableSharedFlow<ReactivePacket<*>>()
    private val nonMutableFlow = subscriptionSharedFlow.asSharedFlow()
    //chunks are a 4x4 grid laid in an array and are absolute position in world
    // x = idx / 4  0: 0,1,2,3  1: 4,5,6,7 ...
    // y = idx mod 4     0: 0,4,8,12 1: 1,5,9,13...
    val chunks = Array<Chunk>(16) { i ->
        Chunk(
            WorldInt2(
                ((i shr 2) + (x shl 2)),
                ((i and 3) + (z shl 2))
            ),
            this
        )
    }

    /**
     * [addPlayer] will subscribe a new player to this relay so when
     * anything happens we notify this new player as well.
     */
    fun addPlayer(player: Player) {
        //player.sendChunk(chunks[0])
        scope.launch {
            nonMutableFlow.filter { player.filter(it) }
                .collectLatest {
                player.emitReactiveCommand(it)
            }
        }
    }

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

    }

    /**
     * [passToNewRelay] allows fast transfer of a player from one relay to another.
     */
    fun passToNewRelay() {

    }

    /**
     * [decommission] safely frees up the memory of this chunk relay.
     */
    fun decommission() {

    }

    override fun toString(): String {
        return "ChunkRelay: x:$x z:$z"
    }
}