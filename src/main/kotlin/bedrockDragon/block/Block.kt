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
import bedrockDragon.registry.Registry
import bedrockDragon.util.aabb.AABB
import bedrockDragon.world.PaletteGlobal
import jdk.jshell.spi.ExecutionControl.NotImplementedException

/**
 * [BlockImpl] is class to access sealed [Block]
 */
class BlockImpl(name: String): Block(name)

/**
 * [Block] is for a dsl object to create new blocks, this is not meant to be extended.
 * @author NATHAN SWANSON
 * @since BETA
 */
@BlockDSL
sealed class Block(var name: String) {

    //comment parameters todo

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

    /**
     * [onInteract] method is called whenever a player right clicks this block.
     *
     * Parameters:
     * - Player
     */
    var onInteract: ((Player) -> Unit)? = null
    /**
     * [onNearbyBlockChanged] method is called whenever a block on any side updates.
     *
     * Parameters:
     * - Block
     */
    var onNearbyBlockChanged: ((Block) -> Unit)? = null
    /**
     * [onRandomTick] method is called on random tick.
     */
    var onRandomTick: (() -> Unit)? = null

    /**
     * [onTick] method is called every world tick.
     */
    var onTick: (() -> Unit)? = null

    /**
     * [onBlockAdded] method is called whenever a player places a block.
     */
    var onBlockAdded: (() -> Unit)? = null

    var blockState: BlockState? = null
    var inventory: Inventory? = null

    var signature: String = ""

    fun build(): Block {
        return this
    }

    fun asItem(): Item {
        return Registry.ITEM_REGISTRY[signature] ?: throw NotImplementedException("")
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
/////////////////////////////////////////// DSL //////////////////////////////////////
/**
 * [registerBlock] is the DSL builder for creating and registering blocks to the server.
 */
@BlockDSL
fun registerBlock(modName: String, registerList: RegisterBlock.() -> Unit) {
    RegisterBlock(modName).run(registerList)
}


/**
 * [RegisterBlock] DSL class for registering multiple blocks.
 * @see [registerBlock] to call this class.
 * @author Nathan Swanson
 * @since BETA
 */
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
        block.signature = "$modName:${block.name}"
        PaletteGlobal.blockRegistry[block.signature] = block
    }
}