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

package bedrockDragon.item

import bedrockDragon.entity.ItemEntity
import bedrockDragon.network.raknet.protocol.game.entity.AddItemEntityPacket
import bedrockDragon.player.Player
import bedrockDragon.registry.DSLBase
import bedrockDragon.registry.Registry
import bedrockDragon.resource.RuntimeItemState
import dev.romainguy.kotlin.math.Float3

/**
 * [Item] is for a dsl object to create new items, this is not meant to be extended.
 * @author NATHAN SWANSON
 * @since BETA
 */
@ItemDSL
sealed class Item(var name: String = "item"): DSLBase(){

    var alias: String? = null
    //class fields
    var runtimeId = -1 //todo const
    var maxStackSize = 64
    var subItems = mutableListOf<Item>()
    //var tag: VanillaItems.ItemTag? = null
    var durability = -1
    var onActivate: ((Player) -> Unit)? = null
    var damage = -1

    //instance fields
    var iDurability = durability
    var count = 1

    //instance custom fields
    var dynamicFields = HashMap<String, Any>()

    fun dropItem(player: Player, position: Float3, vel: Float3): Boolean {
        val itemEnt = ItemEntity(this@Item).runtimeEntityId
        player.nettyQueue.add(
             AddItemEntityPacket().let {
                it.entityIdSelf = itemEnt
                it.runtimeId = itemEnt
                it.pos = position
                it.velocity = vel
                it.item = this
                //meta
                it.isFromFishing = false
                it.gamePacketBlocking()
            }
        )
        return true
    }

    override fun clone(): Item {
        return ItemImpl(name).let {
            it.runtimeId = runtimeId
            it.maxStackSize = maxStackSize
            it.subItems = subItems
            it.durability = durability
            it.onActivate = onActivate
            it.damage = damage
            it
        }
    }

    operator fun plus(other: Item): Item {
        return other.let { it.count += count; it }
    }

    fun ofType(other : Item): Boolean {
        return name == other.name
    }

    /**
     * [of] is when you want to create sub-items of a shared object. In example of this is the many different pickaxes that all are similar.
     */
    @ItemDSL
    fun of(item: ItemImpl.() -> Unit) {
        val newItem = this.clone() as ItemImpl
        newItem.apply(item)
        //if the parent name has a * then concat parent and child name otherwise just use child name.
        if(name.contains("*")) {
            newItem.name = name.replace("*", newItem.name)
        }
        subItems.add(newItem)
    }
}

/**
 * [ItemImpl] is class to access sealed [Item]
 */
class ItemImpl(name: String): Item(name)

/**
 * [registerItem] is the DSL builder for creating and registering blocks to the server.
 */
@ItemDSL
fun registerItem(modName: String, registerList: RegisterItem.() -> Unit) {
    RegisterItem(modName).run(registerList)
}


/**
 * [RegisterItem] DSL class for registering multiple items.
 * @see [RegisterItem] to call this class.
 * @author Nathan Swanson
 * @since BETA
 */
@ItemDSL
class RegisterItem(var modName: String) {

    @ItemDSL
    fun item(name: String, lambda: Item.() -> Unit = {}) {
        val item = ItemImpl(name).apply(lambda)
        if(item.subItems.isNotEmpty()) {
            item.subItems.forEach {
                it.name = "$modName:${it.name}"
                Registry.ITEM_REGISTRY[it.name] = it
                ensureRuntimeIdAlloc(it)
            }
        } else {

            item.name = "$modName:${item.name}"
            Registry.ITEM_REGISTRY[item.name] = item

            if(item.alias != null) {
                Registry.ITEM_REGISTRY["$modName:${item.alias}"] = item
            }
            ensureRuntimeIdAlloc(item)
        }
    }

    private fun ensureRuntimeIdAlloc(item: Item) {
        if(item.runtimeId == -1) {
            item.runtimeId = RuntimeItemState.getRuntimeIdFromName(item.name)
        } else {
            //todo
        }
    }
}