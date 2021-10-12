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

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.Failable
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.RakNetException
import bedrockDragon.network.raknet.handler.PacketConstants

/**
 * An `UNCONNECTED_PING` packet.
 *
 *
 * This packet is sent by clients either by broadcasting to the local network or
 * sending directly to servers in order to get their status and descriptor, also
 * known as an [Identifier][bedrockDragon.network.raknet.identifier.Identifier].
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
open class UnconnectedPing : RakNetPacket, Failable {
    /**
     * The timestamp of the sender.
     */
	@JvmField
	var timestamp: Long = 0

    /**
     * Whether or not the magic bytes read in the packet are valid.
     */
	@JvmField
	var magic = false

    /**
     * The client's ping ID.
     */
	@JvmField
	var pingId: Long = 0

    /**
     * The client's connection type.
     */
	@JvmField
	var connectionType: ConnectionType? = null

    /**
     * Whether or not the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates an `UNCONNECTED_PING` packet to be encoded.
     *
     * @param requiresOpenConnections
     * `true` if the server should only respond if it has
     * open connections available, `false` if the server
     * should unconditionally respond.
     * @see .encode
     */
    protected constructor(requiresOpenConnections: Boolean) : super((if (requiresOpenConnections) PacketConstants.UNCONNECTED_REQUIRE_OPEN_PING else PacketConstants.UNCONNECTED_PING).toInt()) {}

    /**
     * Creates an `UNCONNECTED_PING` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : this(false) {}

    /**
     * Creates an `UNCONNECTED_PING` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        try {
            writeLong(timestamp)
            writeMagic()
            writeLong(pingId)
            this.writeConnectionType(connectionType)
        } catch (e: RakNetException) {
            timestamp = 0
            magic = false
            pingId = 0
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun decode() {
        try {
            timestamp = readLong()
            magic = readMagic()
            pingId = readLong()
            connectionType = readConnectionType()
        } catch (e: RakNetException) {
            timestamp = 0
            magic = false
            pingId = 0
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}