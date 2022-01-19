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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.network.raknet.protocol.game

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.VarInt
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.type.AttributeBR
import bedrockDragon.network.raknet.protocol.game.type.gamerule.GameRules
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

open class PacketPayload(val id: Int): Packet() {
    var reliability: Reliability? = null

    open fun encode() {}
    open fun decode(packet: Packet) {}
    override fun writeString(s: String?): Packet {
        if(size() > 0) {
            writeUnsignedVarInt(s!!.length)
            write(*s.toByteArray())
        } else {
            writeUnsignedVarInt(0)
        }
        return this
    }


    fun writeGameRules(gameRules: GameRules) {
        val rules = gameRules.gameRules
        writeUnsignedVarInt(rules.size)
        rules.forEach {(gameRule, value) ->
            writeString(gameRule.s)
            writeBoolean(true) //can be changed

            //unknown 0
            //boolean 1
            //Integer 2
            //Float 3
            val typeId = when(value.key) {
                is Boolean -> 1
                is Int -> 2
                is Float -> 3
                else -> 0
            }

            VarInt.writeUnsignedVarInt(typeId, outputStream)

            //todo add inline functions for all types so we don't do this

            when(typeId) {
                1 -> writeBoolean(value.key as Boolean)
                2 -> writeUnsignedVarInt(value.key as Int)
                3 -> writeFloatLE(value.key as Double)
            }
        }
    }



    fun writeVector2(vector: Float2) {
        writeFloatLE(vector.x.toDouble())
        writeFloatLE(vector.y.toDouble())
    }

    fun writeBlockCoordinates(vector: Float3) {
        writeVarInt(vector.x.toInt())
        writeUnsignedVarInt(vector.y.toInt())
        writeVarInt(vector.z.toInt())
    }

    fun writeSignedBlockCoordinates(vector: Float3) {
        writeVarInt(vector.x.toInt())
        writeVarInt(vector.y.toInt())
        writeVarInt(vector.z.toInt())
    }

    fun gamePacket() : MinecraftPacket {
        encode()
        return MinecraftPacket.encapsulateGamePacket(this, id, reliability)
    }

    fun writeAttribute(attribute: AttributeBR.Attribute) {
        writeFloatLE(attribute.minValue.toDouble())
        writeFloatLE(attribute.maxValue.toDouble())
        writeFloatLE(attribute.getValueOrDefault().toDouble())
        writeFloatLE(attribute.defaultValue.toDouble())
        writeString(attribute.name)
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.appendLine(this::class.simpleName)
        this::class.memberProperties.filter { property -> property.visibility == KVisibility.PUBLIC
        }.forEach { try {builder.append("${it.name}: ${it.call(this)}\n")} catch (e: Exception) {} }
        return builder.toString()
    }
}
