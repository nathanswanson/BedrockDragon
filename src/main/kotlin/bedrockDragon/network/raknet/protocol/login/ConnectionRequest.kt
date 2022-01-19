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
package bedrockDragon.network.raknet.protocol.login

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketConstants

/**
 * A `CONNECTION_REQUEST` packet.
 *
 *
 * This is the first packet sent by the client during login after initial
 * connection has succeeded.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class ConnectionRequest : RakNetPacket {
    /**
     * The client's globally unique ID.
     */
	@JvmField
	var clientGuid: Long = 0

    /**
     * The client's timestamp.
     */
	@JvmField
	var timestamp: Long = 0

    /**
     * Whether or not security should be used.
     *
     *
     * Since JRakNet does not have this feature implemented, `false`
     * will always be the value used when sending this value. However, this
     * value can be `true` if it is being set through decoding.
     */
	@JvmField
	var useSecurity = false

    /**
     * Creates a `CONNECTION_REQUEST` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(PacketConstants.CONNECTION_REQUEST) {}

    /**
     * Creates a `CONNECTION_REQUEST` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        useSecurity = false // TODO: Not supported
        writeLong(clientGuid)
        writeLong(timestamp)
        writeBoolean(useSecurity)
    }

    override fun decode() {
        clientGuid = readLong()
        timestamp = readLong()
        useSecurity = readBoolean()
    }
}