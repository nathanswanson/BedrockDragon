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

import bedrockDragon.network.raknet.*
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.type.AttributeBR
import bedrockDragon.network.raknet.protocol.game.type.gamerule.GameRules
import bedrockDragon.player.Player
import bedrockDragon.reactive.ReactivePacket
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

open class PacketPayload(val id: Int): Packet() {
    var reliability: Reliability? = null

    //THREAD
    var encoded = false
    private var threadLock = Semaphore(1)

    open suspend fun encode() {

    }
    open fun decode(packet: Packet) {}
    override fun writeString(s: String?): Packet {
        if(size() > 0) {
            val bytes = s!!.encodeToByteArray()
            writeUnsignedVarInt(bytes.size)
            write(*bytes)
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
    fun gamePacketBlocking(): MinecraftPacket {
        return runBlocking { gamePacket() }
    }

    suspend fun gamePacket() : MinecraftPacket {
        threadLock.acquire()
        if(!encoded) {
            encoded = true
            encode()
        }

        threadLock.release()
        return MinecraftPacket.encapsulateGamePacket(this, id, reliability)
    }


    fun writeAttribute(attribute: AttributeBR.Attribute) {
        writeFloatLE(attribute.minValue)
        writeFloatLE(attribute.maxValue)
        writeFloatLE(attribute.getValueOrDefault())
        writeFloatLE(attribute.defaultValue)
        writeString(attribute.name)
        writeUnsignedVarInt(0) //modifier
    }

    fun writeMetaData(metaTag: MetaTag) {
        writeUnsignedVarInt(metaTag.size())
        metaTag.data.forEach {
            writeUnsignedVarInt(it.key)//id
            writeUnsignedVarInt(it.value.type)
            when(it.value.type) {
                DATA_TYPE_BYTE -> write(it.value.data as Byte)
                DATA_TYPE_SHORT -> writeShortLE((it.value.data as Short).toInt())
                DATA_TYPE_INT -> writeVarInt(it.value.data as Int)
                DATA_TYPE_FLOAT -> writeFloatLE(it.value.data as Float)
                DATA_TYPE_STRING -> writeString(it.value.data as String)
                DATA_TYPE_LONG -> writeVarLong(it.value.data as Long)
            }
        }
    }

    /**
     * [toString] is only for debug do not use this to get data from packet. This function uses reflection and has high overhead.
     */
    override fun toString(): String {
        val builder = StringBuilder()

        builder.appendLine(this::class.simpleName)
        this::class.memberProperties.filter { property -> property.visibility == KVisibility.PUBLIC
        }.forEach { try {builder.append("${it.name}: ${it.call(this)}\n")} catch (_: Exception) {} }
        return builder.toString()
    }
}

