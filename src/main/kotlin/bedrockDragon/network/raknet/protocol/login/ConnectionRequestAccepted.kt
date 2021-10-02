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
import bedrockDragon.network.raknet.RakNet.systemAddressCount
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.Failable
import java.net.InetSocketAddress
import bedrockDragon.network.raknet.RakNet
import java.net.UnknownHostException

/**
 * A `CONNECTION_REQUEST_ACCEPTED` packet.
 *
 *
 * This packet is sent by the server during login after the
 * [CONNECTION_REQUEST][ConnectionRequest] packet to indicate that a
 * client's connection has been accepted.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class ConnectionRequestAccepted : RakNetPacket, Failable {
    /**
     * The address of the client that sent the connection request.
     */
	@JvmField
	var clientAddress: InetSocketAddress? = null

    /**
     * The RakNet system addresses.
     */
    lateinit var systemAddresses: Array<InetSocketAddress>

    /**
     * The client timestamp.
     */
	@JvmField
	var clientTimestamp: Long = 0

    /**
     * The server timestamp.
     */
	@JvmField
	var serverTimestamp: Long = 0

    /**
     * Whether or not the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates a `CONNECTION_REQUEST_ACCEPTED` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(ID_CONNECTION_REQUEST_ACCEPTED.toInt()) {
        for (i in systemAddresses.indices) {
            systemAddresses[i] = RakNet.SYSTEM_ADDRESS
        }
    }

    /**
     * Creates a `CONNECTION_REQUEST_ACCEPTED` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        try {
            this.writeAddress(clientAddress)
            writeShort(0)
            for (i in systemAddresses.indices) {
                this.writeAddress(systemAddresses[i])
            }
            writeLong(clientTimestamp)
            writeLong(serverTimestamp)
        } catch (e: UnknownHostException) {
            clientAddress = null
            clientTimestamp = 0
            serverTimestamp = 0
            this.clear()
            failed = true
        }
    }

    override fun decode() {
        try {
            clientAddress = readAddress()
            readShort() // TODO: Discover usage
            for (i in systemAddresses.indices) {
                if (remaining() >= 16) {
                    systemAddresses[i] = readAddress()
                } else {
                    systemAddresses[i] = RakNet.SYSTEM_ADDRESS
                }
            }
            clientTimestamp = readLong()
            serverTimestamp = readLong()
        } catch (e: UnknownHostException) {
            clientAddress = null
            clientTimestamp = 0
            serverTimestamp = 0
            this.clear()
            failed = true
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}