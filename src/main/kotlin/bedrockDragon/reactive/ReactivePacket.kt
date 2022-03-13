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
import bedrockDragon.network.raknet.protocol.game.player.MovePlayerPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerActionPacket
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
class AnimatePlayer()
class RotatePlayer(payload: MovePlayerPacket, sender: Player) : ReactivePacket<MovePlayerPacket>(payload, sender)
class BreakBlock(payload: PlayerActionPacket, sender: Player) : ReactivePacket<PlayerActionPacket>(payload, sender) {
    override fun run(relay: ChunkRelay) {
        //drop item
        val block = ItemEntity((sender as Player).world.getBlockAt(sender.blockMining).asItem())
        block.position = sender.blockMining
        relay.addEntity(block)
        relay.invoke(DropItem(AddItemEntityPacket().let {
            it.entityIdSelf = block.runtimeEntityId //todo
            it.runtimeId = block.runtimeEntityId //todo
            it.item = block.item
            it.pos = sender.blockMining + Float3(0.5f, 0f, 0.5f)
            it.velocity = Float3(0f,0f,0f)

            it.isFromFishing = false
            it }, sender))
    }


}

class DropItem(payload: AddItemEntityPacket, sender: Player) : ReactivePacket<AddItemEntityPacket>(payload, sender) {
    override fun filter(otherPlayer: Player): Boolean {
        return true
    }
}
