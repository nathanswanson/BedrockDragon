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
package bedrockDragon.network.raknet.protocol.connection

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket

/**
 * An `OPEN_CONNECTION_REQUEST_1` packet.
 *
 *
 * This is the first packet sent by the client to the server during connection.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class OpenConnectionRequestOne : RakNetPacket {
    /**
     * Whether or not the magic bytes read in the packet are valid.
     */
    var magic = false

    /**
     * The client's network protocol version.
     */
    var networkProtocol = 0

    /**
     * The client's maximum transfer unit size.
     */
    var maximumTransferUnit = 0

    /**
     * Creates an `OPEN_CONNECTION_REQUEST_1` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(ID_OPEN_CONNECTION_REQUEST_1.toInt()) {}

    /**
     * Creates an `OPEN_CONNECTION_REQUEST_1` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        writeMagic()
        writeUnsignedByte(networkProtocol)
        pad(maximumTransferUnit - MTU_PADDING)
    }

    override fun decode() {
        magic = readMagic()
        networkProtocol = readUnsignedByte().toInt()
        maximumTransferUnit = remaining() + MTU_PADDING
        skip(remaining())
    }

    companion object {
        /**
         * At the end of this packet in particular, the client pads the packet with
         * the remaining data left according the `maximumTransferUnit`.
         *
         *
         * This value is equivalent to the size of the IP header (20 bytes) and the
         * size of the UDP header (8 bytes) combined.
         */
        private const val MTU_PADDING = 28
    }
}