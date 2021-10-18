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
package bedrockDragon.network.raknet.server

import bedrockDragon.DragonServer
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.concurrent.ConcurrentHashMap
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.Throws
import java.lang.NullPointerException
import io.netty.channel.ChannelHandlerContext
import bedrockDragon.network.raknet.RakNetPacket
import io.netty.channel.socket.DatagramPacket
import mu.KotlinLogging
import java.lang.Exception
import kotlin.math.log

/**
 * Used by the [RakNetServer] with the sole purpose of sending received
 * packets to the server so they can be handled. Any errors that occurs will
 * also be sent to the server to be dealt with.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class RakNetServerHandler(private val server: DragonServer) : ChannelInboundHandlerAdapter() {

    val logger = KotlinLogging.logger {}

    private val blocked: ConcurrentHashMap<InetAddress, BlockedAddress?> = ConcurrentHashMap()
    private var causeAddress: InetSocketAddress? = null

    /**
     * Blocks the IP address. All currently connected clients with the IP
     * address will be disconnected with the same reason that the IP address was
     * blocked.
     *
     * @param address
     * the IP address to block.
     * @param reason
     * the reason the address was blocked. A `null` reason
     * will have `"Address blocked"` be used as the reason
     * instead.
     * @param time
     * how long the address will blocked in milliseconds.
     * @throws NullPointerException
     * if `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun blockAddress(address: InetAddress?, reason: String?, time: Long) {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        }
        blocked[address] = BlockedAddress(time)
        for (client in server.clients) {
            if (client.key == address) {
                server.disconnect(client.value, reason ?: "Address blocked")
            }
        }
        //TODO EVENT

        server.callEvent { listener: RakNetServerListener? ->
            listener!!.onBlock(
                server, address, reason, time
            )
        }


        //logger.info("Blocked address " + address + " due to \"" + reason + "\" for " + time + " milliseconds");
    }

    /**
     * Unblocks the IP address.
     *
     * @param address
     * the IP address to unblock.
     */
    private fun unblockAddress(address: InetAddress?) {
        if (address != null) {
            if (blocked.remove(address) != null) {
                //TODO EVENT

                server.callEvent { listener: RakNetServerListener? ->
                    listener!!.onUnblock(
                        server, address
                    )
                }
                //logger.info("Unblocked address " + address);\


            }
        }
    }

    /**
     * Returns whether or not the IP address is blocked.
     *
     * @param address
     * the IP address.
     * @return `true` if the IP address is blocked,
     * `false` otherwise.
     */
    private fun isAddressBlocked(address: InetAddress): Boolean {
        return blocked.containsKey(address)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is DatagramPacket) {
            // Get packet and sender data
            val datagram = msg
          //  logger.info { VarInt.readUnsignedVarInt(datagram.content().) }
            val sender = datagram.sender()
            val packet = RakNetPacket(datagram)

            // If an exception happens it's because of this address
            causeAddress = sender

            // Check if address is blocked
            if (isAddressBlocked(sender.address)) {
                val status = blocked[sender.address]
                if (!status!!.shouldUnblock()) {
                    datagram.release() // No longer needed
                    return  // Address still blocked
                }
                unblockAddress(sender.address)
            }

            // Handle the packet and release the buffer
            server.handleMessage(sender, packet)
            logger.debug("Sent packet to server and reset datagram buffer read position");
            //TODO EVENT

            server.callEvent { listener: RakNetServerListener? ->
                datagram.content().readerIndex(0) // Reset index
                listener!!.handleNettyMessage(server, sender, datagram.content())
            }


            if (datagram.release() /* No longer needed */) {
                	logger.trace("Released datagram");
            } else {
                	logger.error("Memory leak: Failed to deallocate datagram when releasing it");
            }

            // No exceptions occurred, release the suspect
            causeAddress = null
        } else {
            logger.info { "Message is not datagram" }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        server.handleHandlerException(causeAddress!!, cause)
    }
}