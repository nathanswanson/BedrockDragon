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

package bedrockDragon.reactive
//probably could be enumclass todo
import bedrockDragon.entity.ItemEntity
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.entity.AddItemEntityPacket
import bedrockDragon.network.raknet.protocol.game.entity.RemoveEntityPacket
import bedrockDragon.network.raknet.protocol.game.entity.TakeItemEntityPacket
import bedrockDragon.network.raknet.protocol.game.player.AnimatePacket
import bedrockDragon.network.raknet.protocol.game.player.MovePlayerPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerActionPacket
import bedrockDragon.network.raknet.protocol.game.world.UpdateBlockPacket
import bedrockDragon.player.Player
import bedrockDragon.world.chunk.ChunkRelay
import dev.romainguy.kotlin.math.Float3

/**
 * [ReactivePacket] is a payload for when an observed event is activated and contains the data for ISubscribers
 * to use.
 * @author Nathan Swanson
 * @since ALPHA
 */
abstract class ReactivePacket<T: PacketPayload>(val payload: T, val sender: ISubscriber) {
    var tags = emptyArray<Any>()
    var priority = 0

   open fun run(relay: ChunkRelay) {

   }

    open fun filter(otherPlayer: Player): Boolean {
        return otherPlayer != sender
    }
}

class MovePlayer(payload: MovePlayerPacket, sender: Player) : ReactivePacket<MovePlayerPacket>(payload, sender)
class AnimatePlayer(payload: AnimatePacket, sender: Player) : ReactivePacket<AnimatePacket>(payload, sender)

class TakeItem(payload: TakeItemEntityPacket, sender: Player) : ReactivePacket<TakeItemEntityPacket>(payload, sender) {
    override fun filter(otherPlayer: Player): Boolean {
        return true
    }
}

class RemoveEntity(payload: RemoveEntityPacket, sender: Player) : ReactivePacket<RemoveEntityPacket>(payload, sender) {
    override fun filter(otherPlayer: Player): Boolean {
        return true
    }
}

class Sneak(payload: PlayerActionPacket, sender: Player): ReactivePacket<PlayerActionPacket>(payload,sender)
class UpdateBlock(payload: UpdateBlockPacket, sender: Player) : ReactivePacket<UpdateBlockPacket>(payload, sender)
class BreakBlock(payload: PlayerActionPacket, sender: Player) : ReactivePacket<PlayerActionPacket>(payload, sender) {
    override fun run(relay: ChunkRelay) {
        //drop item
        val block = ItemEntity((sender as Player).world.getBlockAt(sender.blockMining).asItem())
        block.position = sender.blockMining + Float3(0.5f, 0f, 0.5f) //moves entity to center of block
        //temp
        relay.invoke(UpdateBlock(UpdateBlockPacket().let {
            it.blockRuntimeId = 134
            it.coordinates = sender.blockMining
            it
        },sender))
        //
        relay.addEntity(block)
    }


    override fun filter(otherPlayer: Player): Boolean {
        return false
    }
}

class DropItem(payload: AddItemEntityPacket, sender: Player) : ReactivePacket<AddItemEntityPacket>(payload, sender) {
    override fun filter(otherPlayer: Player): Boolean {
        return true
    }
}
