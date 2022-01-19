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

package bedrockDragon.network.raknet.protocol.game

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.VarInt
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.zlib.PacketCompression


/**
 * Encapsulates all minecraft packets.
 * encodes game_packet id and encrypted payload
 * @author Nathan Swanson
 * @since ALPHA
 */
class MinecraftPacket() : Packet() {
    lateinit var payload: Packet
    var packetId = -1
    var header = -1
    var reliability: Reliability = Reliability.RELIABLE_ORDERED
    /**
     *  GamePacket: 0xFE
     *  Compressed:
     *      payload.size()
     *      packetId
     *      payload.array()
     *  @author Nathan Swanson
     *  @since ALPHA
     */
    fun encode() {
        writeUnsignedByte(PacketConstants.GAME_PACKET)
        //encrypt

        val encryptedLoad = Packet()
        if(packetId >128) {
            encryptedLoad.writeUnsignedVarInt(payload.size() + 2)
        } else {
            encryptedLoad.writeUnsignedVarInt(payload.size() + 1)
        }
        encryptedLoad.writeUnsignedVarInt(packetId)
        encryptedLoad.write(*payload.read(payload.size()))
        write(*PacketCompression.compress(encryptedLoad.read(encryptedLoad.size()), 7))
        //packet gets sent compressed
    }



    fun decode() {
        //packet must be decompressed
        //Don't need to read for game packet taken out already
        header = readUnsignedVarInt().toInt()
        packetId = readUnsignedByte().toInt()
        payload = Packet(buffer())
    }


    companion object {
        /**
         * This is a helper method that shorthands making encapsulated packets.
         * However, this function is rarely called and you most likely want to use
         * PacketPayload$gamePacket() instead
         * @author Nathan Swanson
         * @since ALPHA
         */
        fun encapsulateGamePacket(payload: Packet, id: Int, reliability: Reliability?): MinecraftPacket {
            val packet = MinecraftPacket()
            packet.payload = payload
            packet.packetId = id

            if (reliability != null) {
                packet.reliability = reliability
            }

            packet.encode()

            return packet
        }
    }
}