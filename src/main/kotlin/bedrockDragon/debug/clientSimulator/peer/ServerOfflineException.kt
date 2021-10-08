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
package bedrockDragon.debug.clientSimulator.peer

import bedrockDragon.debug.clientSimulator.RakNetClient
import java.net.InetSocketAddress


/**
 * Signals that a [RakNetClient] attempted to to connect to an offline
 * server.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.0.0
 */
class ServerOfflineException constructor(client: RakNetClient?, address: InetSocketAddress) :
    PeerFactoryException(client, "Server at address " + address.toString() + " is offline") {
    private val address: InetSocketAddress

    /**
     * Returns the address of the offline server.
     *
     * @return the address of the offline server.
     */
    fun getAddress(): InetSocketAddress {
        return address
    }

    companion object {
        private val serialVersionUID: Long = -3916155995964791602L
    }

    /**
     * Constructs a `ServerOfflineException`.
     *
     * @param client
     * the client that attempted to the offline server.
     * @param address
     * the address of the offline server.
     */
    init {
        this.address = address
    }
}