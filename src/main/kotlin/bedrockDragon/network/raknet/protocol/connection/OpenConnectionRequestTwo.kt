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
import java.net.InetSocketAddress
import bedrockDragon.network.raknet.RakNetException
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Failable
import java.net.UnknownHostException

/**
 * An `OPEN_CONNECTION_REQUEST_2` packet.
 *
 *
 * This packet is sent by the client to the server after receiving a
 * [OPEN_CONNECTION_RESPONSE_1][OpenConnectionResponseOne] packet.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class OpenConnectionRequestTwo : RakNetPacket, Failable {
    /**
     * Whether or not the magic bytes read in the packet are valid.
     */
    var magic = false

    /**
     * The address of the server that the client wishes to connect to.
     */
    var serverAddress: InetSocketAddress? = null

    /**
     * The maximum transfer unit size the client and the server have agreed
     * upon.
     */
    var maximumTransferUnit = 0

    /**
     * The client's globally unique ID.
     */
    var clientGuid: Long = 0

    /**
     * The client connection type.
     */
    var connectionType: ConnectionType? = null

    /**
     * Whether or not the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates an `OPEN_CONNECTION_REQUEST_2` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_2) {}

    /**
     * Creates an `OPEN_CONNECTION_REQUEST_2` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        try {
            writeMagic()
            this.writeAddress(serverAddress)
            writeUnsignedShort(maximumTransferUnit)
            writeLong(clientGuid)
            this.writeConnectionType(connectionType)
        } catch (e: UnknownHostException) {
            magic = false
            serverAddress = null
            maximumTransferUnit = 0
            clientGuid = 0
            connectionType = null
            this.clear()
            failed = true
        } catch (e: RakNetException) {
            magic = false
            serverAddress = null
            maximumTransferUnit = 0
            clientGuid = 0
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun decode() {
        try {
            magic = readMagic()
            serverAddress = readAddress()
            maximumTransferUnit = readUnsignedShort().toInt()
            clientGuid = readLong()
            connectionType = readConnectionType()
        } catch (e: UnknownHostException) {
            magic = false
            serverAddress = null
            maximumTransferUnit = 0
            clientGuid = 0
            connectionType = null
            this.clear()
            failed = true
        } catch (e: RakNetException) {
            magic = false
            serverAddress = null
            maximumTransferUnit = 0
            clientGuid = 0
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}