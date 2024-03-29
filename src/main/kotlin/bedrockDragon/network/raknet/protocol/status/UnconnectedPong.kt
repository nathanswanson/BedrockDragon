/*
 *    __     ______     ______     __  __     __   __     ______     ______  
 *   /\ \   /\  == \   /\  __ \   /\ \/ /    /\ "-.\ \   /\  ___\   /\__  _\
 *  _\_\ \  \ \  __<   \ \  __ \  \ \  _"-.  \ \ \-.  \  \ \  __\   \/_/\ \/  
 * /\_____\  \ \_\ \_\  \ \_\ \_\  \ \_\ \_\  \ \_\\"\_\  \ \_____\    \ \_\ 
 * \/_____/   \/_/ /_/   \/_/\/_/   \/_/\/_/   \/_/ \/_/   \/_____/     \/_/                                                                          
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2020 "Whirvis" Trent Summerlin
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
package bedrockDragon.network.raknet.protocol.status

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.RakNetException
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.identifier.MinecraftServerMessage
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Failable
import bedrockDragon.resource.ServerProperties

/**
 * An `UNCONNECTED_PONG` packet.
 *
 *
 * This packet is sent in response to [UNCONNECTED_PING][UnconnectedPing]
 * and [UNCONNECTED_PING_OPEN_CONNECTIONS][UnconnectedPingOpenConnections]
 * packets in order to give the client server information and show that it is
 * online.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet 1.0.0
 */
class UnconnectedPong : RakNetPacket(PacketConstants.UNCONNECTED_PONG), Failable {
    /**
     * The timestamp sent in the ping packet.
     */
    var timestamp: Long = 0

    /**
     * The server's pong ID.
     */
    var pongId: Long = 0

    /**
     * Whether or not the magic bytes read in the packet are valid.
     */
    var magic = false

    /**
     * The server's identifier.
     */
	var identifier = MinecraftServerMessage(ServerProperties.getProperty("motd"))

    /**
     * The server's connection type.
     */
    var connectionType: ConnectionType? = null

    /**
     * Whether the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates an `UNCONNECTED_PONG` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */

    override fun encode() {
        try {
            writeLong(timestamp)
            writeLong(pongId)
            writeMagic()
            writeString(identifier.motd.toString())
            this.writeConnectionType(connectionType)
        } catch (e: RakNetException) {
            timestamp = 0
            pongId = 0
            magic = false
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}