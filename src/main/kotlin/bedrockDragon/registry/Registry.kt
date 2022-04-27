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

package bedrockDragon.registry

import bedrockDragon.command.Command
import bedrockDragon.entity.Entity
import bedrockDragon.item.Item
import bedrockDragon.world.World

/**
 * [Registry] parent class for block,item, world... etc registries.
 * @author Nathan Swanson
 * @since BETA
 */
open class Registry<T,K : DSLBase> {
    protected val registeredValues = HashMap<T, K>()


    /**
     * [get] returns an instance of the desired object. This is an operator function and should be called like the object is an array
     *
     * val registry = Registry<String, Item>()
     *
     *
     * val sword = registry&#91;minecraft:wooden_sword&#93;
     */
     @Suppress("UNCHECKED_CAST")
    open operator fun get(value: T): K? {
        return registeredValues[value]?.let {
             (registeredValues[value] as DSLBase).clone() as K
        }
    }

    fun containsKey(value: T) : Boolean {
        return registeredValues.containsKey(value)
    }

    /**
     * [getObject] returns the original object. Typically, this should only be used to overwrite the requested object.
     * @see [get] for getting an instance of an object i.e. when you want to spawn an item.
     */
    fun getObject(value: T): K? {
        return registeredValues[value]
    }

    fun register(id: T, registerObject: K): Boolean {
        return registeredValues.putIfAbsent(id, registerObject) == null
    }

    operator fun set(name: T, value: K) {
        registeredValues[name] = value
    }

    fun size(): Int {
        return registeredValues.size
    }

    fun allEntries(): HashMap<T, K> {
        return registeredValues
    }

    companion object {
        val COMMAND_REGISTRY = Registry<String, Command>()
        val WORLD_REGISTRY = object: Registry<Int, World>() {
            override fun get(value: Int): World {
                return getObject(value) ?: (registeredValues[0] as World) //todo null check
            }
        }
        val ITEM_REGISTRY = Registry<String, Item>()
        val ENTITY_REGISTRY = Registry<String, Entity>()
    }
}