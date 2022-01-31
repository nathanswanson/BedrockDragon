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
 * Copyright (c) 2021-2022 Nathan Swanson
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

package bedrockDragon.block

import bedrockDragon.block.blockState.BlockState
import bedrockDragon.inventory.Inventory
import bedrockDragon.item.Item
import bedrockDragon.player.Player
import bedrockDragon.util.aabb.AABB
import bedrockDragon.world.PaletteGlobal


class BlockImpl(name: String): Block(name)

@BlockDSL
sealed class Block(var name: String) {

    //this will be provided by the builder constructor

    var stackSize = 64
    var breakTool: Item? = null
    var blastResistance = 0.0
    var hardness = 0.0
    var luminant = false
    var transparent = false
    var flammable = false
    var drops = emptyArray<Item>()
    var gravity = GravityEffect.FLOAT
    var runtimeId = -1
    var aabb: AABB = AABB.CUBE()
    //todo texture tag
    //todo recipe builder
    //todo runtimeId assign
    //events

    var onInteract: ((Player) -> Unit)? = null
    var onNearbyBlockChanged: (() -> Unit)? = null
    var onRandomTick: (() -> Unit)? = null
    var onTick: (() -> Unit)? = null
    var onBlockAdded: (() -> Unit)? = null

    var blockState: BlockState? = null
    var inventory: Inventory? = null
    fun build(): Block {
        return this
    }

    enum class GravityEffect {
        FLOAT,
        FALL,
        BREAK
    }

    override fun toString(): String {
        return """Block: $name"""
    }



}

@BlockDSL
fun registerBlock(modName: String, registerList: RegisterBlock.() -> Unit) {
    RegisterBlock(modName).run(registerList)
}


@BlockDSL
class RegisterBlock(var modName: String) {

    @BlockDSL
    fun block(name: String, lambda: Block.() -> Unit = {}) {
        val block = BlockImpl(name).apply(lambda)
        if(block.runtimeId == -1) {
            block.runtimeId = PaletteGlobal.getRuntimeIdFromName("$modName:${block.name}")
        } else {
            //PaletteGlobal.globalBlockPalette["$modName:${block.name}"] = block.runtimeId
            //todo create palette entry
        }
        PaletteGlobal.blockRegistry["$modName:${block.name}"] = block
    }
}