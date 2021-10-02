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
 * A `NEW_INCOMING_CONNECTION` packet.
 *
 *
 * This is sent by the client after receiving the
 * [CONNECTION_REQUEST_ACCEPTED][ConnectionRequestAccepted] packet.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class NewIncomingConnection : RakNetPacket, Failable {
    /**
     * The server address.
     */
    @JvmField
    var serverAddress: InetSocketAddress? = null

    /**
     * The RakNet system addresses.
     */
    private lateinit var systemAddresses: Array<InetSocketAddress?>

    /**
     * The server timestamp.
     */
    @JvmField
    var serverTimestamp: Long = 0

    /**
     * The client timestamp.
     */
    @JvmField
    var clientTimestamp: Long = 0

    /**
     * Whether or not the packet failed to encode/decode.
     */
    private var failed = false

    /**
     * Creates a `NEW_INCOMING_CONNECTION` packet to be encoded.
     *
     * @see .encode
     */
    constructor() : super(ID_NEW_INCOMING_CONNECTION.toInt()) {
        systemAddresses = arrayOfNulls(systemAddressCount)
        for (i in systemAddresses.indices) {
            systemAddresses[i] = RakNet.SYSTEM_ADDRESS
        }
    }

    /**
     * Creates a `NEW_INCOMING_CONNECTION` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        try {
            this.writeAddress(serverAddress)
            for (i in systemAddresses.indices) {
                this.writeAddress(systemAddresses[i])
            }
            writeLong(serverTimestamp)
            writeLong(clientTimestamp)
        } catch (e: UnknownHostException) {
            failed = true
            serverAddress = null
            serverTimestamp = 0
            clientTimestamp = 0
            this.clear()
        }
    }

    override fun decode() {
        try {
            serverAddress = readAddress()
            systemAddresses = arrayOfNulls(systemAddressCount)
            for (i in systemAddresses.indices) {
                if (remaining() > java.lang.Long.SIZE + java.lang.Long.SIZE) {
                    systemAddresses[i] = readAddress()
                } else {
                    systemAddresses[i] = RakNet.SYSTEM_ADDRESS
                }
            }
            serverTimestamp = readLong()
            clientTimestamp = readLong()
        } catch (e: UnknownHostException) {
            failed = true
            serverAddress = null
            serverTimestamp = 0
            clientTimestamp = 0
            this.clear()
        }
    }

    override fun failed(): Boolean {
        return failed
    }
}