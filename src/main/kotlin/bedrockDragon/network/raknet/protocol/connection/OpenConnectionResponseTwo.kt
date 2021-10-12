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
import bedrockDragon.network.raknet.protocol.Failable
import java.net.InetSocketAddress
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.RakNetException
import bedrockDragon.network.raknet.handler.PacketConstants
import java.io.IOException

/**
 * An `OPEN_CONNECTION_REQUEST_2` packet.
 *
 *
 * This is sent by the server to the client after receiving a
 * [OPEN_CONNECTION_REQUEST_2][OpenConnectionRequestTwo] packet.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class OpenConnectionResponseTwo : RakNetPacket, Failable {
    /**
     * Whether or not the magic bytes read in the packet are valid.
     */
    var magic = false

    /**
     * The server's globally unique ID.
     */
    var serverGuid: Long = 0

    /**
     * The address of the client.
     */
    var clientAddress: InetSocketAddress? = null

    /**
     * The maximum transfer unit size the server and the client have agreed
     * upon.
     */
    var maximumTransferUnit = 0

    /**
     * Whether or not encryption is enabled.
     *
     *
     * Since JRakNet does not have this feature implemented, `false`
     * will always be the value used when sending this value. However, this
     * value can be `true` if it is being set through decoding.
     */
    var encryptionEnabled = false

    /**
     * The server connection type.
     */
    var connectionType: ConnectionType? = null

    /**
     * Whether or not the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates an `OPEN_CONNECTION_RESPONSE_2` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(PacketConstants.SERVER_TO_CLIENT_HANDSHAKE_2) {}

    /**
     * Creates an `OPEN_CONNECTION_RESPONSE_2` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        try {
            encryptionEnabled = false // TODO: Not supported
            writeMagic()
            writeLong(serverGuid)
            this.writeAddress(clientAddress)
            writeUnsignedShort(maximumTransferUnit)
            writeBoolean(encryptionEnabled)
            this.writeConnectionType(connectionType)
        } catch (e: IOException) {
            magic = false
            serverGuid = 0
            clientAddress = null
            maximumTransferUnit = 0
            encryptionEnabled = false
            connectionType = null
            this.clear()
            failed = true
        } catch (e: RakNetException) {
            magic = false
            serverGuid = 0
            clientAddress = null
            maximumTransferUnit = 0
            encryptionEnabled = false
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun decode() {
        try {
            magic = readMagic()
            serverGuid = readLong()
            clientAddress = readAddress()
            maximumTransferUnit = readUnsignedShort().toInt()
            encryptionEnabled = readBoolean()
            connectionType = readConnectionType()
        } catch (e: IOException) {
            magic = false
            serverGuid = 0
            clientAddress = null
            maximumTransferUnit = 0
            encryptionEnabled = false
            connectionType = null
            this.clear()
            failed = true
        } catch (e: RakNetException) {
            magic = false
            serverGuid = 0
            clientAddress = null
            maximumTransferUnit = 0
            encryptionEnabled = false
            connectionType = null
            this.clear()
            failed = true
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}