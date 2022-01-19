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

package bedrockDragon.inventory

import bedrockDragon.Item
import bedrockDragon.network.raknet.protocol.game.inventory.InventoryContentPacket
import bedrockDragon.player.Player

/**
 * [Inventory] is a basic abstract class for all Inventories in Minecraft.
 * @author Nathan Swanson
 * @since ALPHA
 */
abstract class Inventory(val size: Int) {
    private val viewers = ArrayList<Player>()
    internal val slots: Array<Item?> = arrayOfNulls(size)


    var type = -1
    val windowId = 0

    fun contains(item: Item) {
        slots.contains(item)
    }

    fun getContents(): Array<Item?> {
        return slots
    }

    /**
     * [addItem] attempts to add the inputed item in the first available slot.
     * If no slot is available nothing happens and the method returns false.
     */
    fun addItem(item: Item): Boolean {
        return try {
            slots[slots.indexOfFirst { it == null }] = item
            true
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    /**
     * [addItem] attempts to add the inputed item to the given slot but returns false if it is filled, or
     * is an invalid slot.
     */
    fun addItem(item: Item, slot: Int): Boolean {
        //slot > inventory size
        if(slot >= size)
            throw IndexOutOfBoundsException("Can not place ${item} in slot ${slot} because is larger than" +
                    "the size of the inventory ($size)")

        if(slots[slot] == null) {
            slots[slot] = item
            return true
        }
        return false
    }

    /**
     * [forceAdd] will set the inputed slot with the inputed item regardless if something already exists there.
     *
     */
    fun forceAdd(item: Item, slot: Int): Boolean {
        //slot > inventory size
        if(slot >= size)
            throw IndexOutOfBoundsException("Can not place ${item} in slot ${slot} because is larger than" +
                    "the size of the inventory ($size)")

        slots[slot] = item
        return true
    }


    /**
     * [clear] will completely remove the contents of an inventory
     */
    fun clear() {
        slots.fill(null)
    }

    fun addViewer(player: Player) {
        viewers.add(player)
    }

    fun isOpenedBy(player: Player): Boolean {
        return viewers.contains(player)
    }
    abstract fun openInventory()
}