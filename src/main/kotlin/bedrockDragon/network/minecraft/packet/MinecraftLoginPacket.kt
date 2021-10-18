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

package bedrockDragon.network.minecraft.packet

import bedrockDragon.network.JWT
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.VarInt
import io.fusionauth.jwt.JWTDecoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.nio.charset.StandardCharsets

class MinecraftLoginPacket(val packet: Packet): MinecraftPacket(packet) {

    var protocol = 0
    lateinit var chainData: JsonElement
    lateinit var skinData: String

    override fun encode() {

    }

    override fun decode() {
        protocol = packet.readInt()
        if(protocol == 0) {
            packet.buffer().readerIndex(packet.buffer().readerIndex()+2)
            protocol = packet.readInt()
        }

        //chain data

        //don't know what this is yet
        VarInt.readUnsignedVarInt(packet.inputStream)

        val buf = packet.buffer()

        val chainByteIdx = buf.readIntLE()

        val chainString = Json.decodeFromString<ChainData>(buf.slice(buf.readerIndex(), chainByteIdx).toString(StandardCharsets.UTF_8))

        //I skip 4 because I don't need the LEInt anyways
        buf.readerIndex(buf.readerIndex() + chainByteIdx + 4)

        //TODO SKIN PARSER
         skinData = JWT(buf.toString(StandardCharsets.UTF_8)).payload

        //I don't understand this yet. Client sends 2 seemingly useless JWT then a third with the content I need

        for(c in chainString.chain) {
            val decodedJWT = JWT(c)
            if(decodedJWT.payload.contains("extraData")) {
                val decoded = Json.decodeFromString<ExtraData>(decodedJWT.payload)
                chainData = decoded.extraData

            }
        }


    }


    @Serializable
    data class ChainData(val chain: Array<String>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChainData

            if (!chain.contentEquals(other.chain)) return false

            return true
        }

        override fun hashCode(): Int {
            return chain.contentHashCode()
        }
    }

    @Serializable
    data class SkinData(val skin: String)

    @Serializable
    data class ExtraData(val nbf: Long, val extraData: JsonElement, val randomNonce: Long, val iss: String, val exp: Long, val iat: Long, val identityPublicKey: String)
}
