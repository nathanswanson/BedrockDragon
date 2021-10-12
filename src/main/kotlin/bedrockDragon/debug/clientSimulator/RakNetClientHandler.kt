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
package bedrockDragon.debug.clientSimulator

import bedrockDragon.network.raknet.RakNetPacket
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

/**
 * Used by the [RakNetClient] with the sole purpose of sending received
 * packets to the client so they can be handled. Any errors that occurs will
 * also be sent to the client to be dealt with.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class RakNetClientHandler(client: RakNetClient) : ChannelInboundHandlerAdapter() {
    private val client: RakNetClient
    private var causeAddress: InetSocketAddress? = null
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is DatagramPacket) {
            // Get packet and sender data
            val datagram = msg
            val sender: InetSocketAddress = datagram.sender()
            val packet = RakNetPacket(datagram)

            // If an exception happens it's because of this address
            causeAddress = sender
            // Handle the packet and release the buffer
            client.handleMessage(sender, packet)
            client.callEvent { listener: RakNetClientListener ->
                datagram.content().readerIndex(0) // Reset position
                listener.handleNettyMessage(client, sender, datagram.content())
            }
            if (datagram.release() /* No longer needed */) {
            } else {
            }

            // No exceptions occurred, release the suspect
            causeAddress = null
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        client.handleHandlerException(causeAddress, cause)
    }

    /**
     * Creates a RakNet client Netty handler.
     *
     * @param client
     * the client to send received packets to.
     */
    init {
        this.client = client
    }
}