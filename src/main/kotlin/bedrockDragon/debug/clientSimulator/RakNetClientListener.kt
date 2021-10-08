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
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import com.whirvis.jraknet.peer.RakNetServerPeer
import io.netty.buffer.ByteBuf
import java.net.InetSocketAddress

/**
 * Used to listen for events that occur in the [RakNetClient]. In order to
 * listen for events, one must use the
 * [RakNetClient.addListener] method.
 *
 *
 * Event methods are called on the same thread that called them. Typically, this
 * is the NIO event loop group that the client is using, or the client thread
 * itself. This normally does not matter, however in some cases if a listener
 * takes too long to respond (typically
 * {@value com.whirvis.jraknet.peer.RakNetPeer#PEER_TIMEOUT} milliseconds) then
 * the client can actually timeout.
 *
 *
 * To have event methods called on their own dedicated thread, annotate the
 * listening class with the [ ThreadedListener][com.whirvis.jraknet.ThreadedListener] annotation.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
interface RakNetClientListener {
    /**
     * Called when the client connects to a server.
     *
     * @param client
     * the client.
     * @param address
     * the server address.
     * @param connectionType
     * the connection type of the server.
     */
    fun onConnect(client: RakNetClient?, address: InetSocketAddress?, connectionType: ConnectionType?) {}

    /**
     * Called when the client has logged in to a server.
     *
     * @param client
     * the client.
     * @param peer
     * the server.
     */
    fun onLogin(client: RakNetClient?, peer: RakNetServerPeer?) {}

    /**
     * Called when the client disconnects from the server.
     *
     * @param client
     * the client.
     * @param address
     * the address of the server.
     * @param peer
     * the server that the client disconnected from,
     * `null` if login has not yet finished.
     * @param reason
     * the reason for disconnection.
     */
    fun onDisconnect(
        client: RakNetClient?, address: InetSocketAddress?, peer: RakNetServerPeer?,
        reason: String?
    ) {
    }

    /**
     * Called when a message is acknowledged by the server.
     *
     * @param client
     * the client.
     * @param peer
     * the server that acknowledged the packet.
     * @param record
     * the acknowledged record.
     * @param packet
     * the acknowledged packet.
     */
    fun onAcknowledge(
        client: RakNetClient?, peer: RakNetServerPeer?, record: Record?,
        packet: EncapsulatedPacket?
    ) {
    }

    /**
     * Called when a message is lost by the server.
     *
     * @param client
     * the client.
     * @param peer
     * the server that lost the packet.
     * @param record
     * the lost record.
     * @param packet
     * the lost packet.
     */
    fun onLoss(client: RakNetClient?, peer: RakNetServerPeer?, record: Record?, packet: EncapsulatedPacket?) {}

    /**
     * Called when a packet from the server has been received and is ready to be
     * handled.
     *
     * @param client
     * the client.
     * @param peer
     * the server that sent the packet.
     * @param packet
     * the received packet.
     * @param channel
     * the channel the packet was sent on.
     */
    fun handleMessage(client: RakNetClient?, peer: RakNetServerPeer?, packet: RakNetPacket?, channel: Int) {}

    /**
     * Called when a packet with an ID below `ID_USER_PACKET_ENUM`
     * cannot be handled by the [RakNetServerPeer] because it is not
     * programmed to handle it.
     *
     *
     * This function can be used to add missing features from the regular RakNet
     * protocol that are absent in JRakNet if needed.
     *
     * @param client
     * the client.
     * @param peer
     * the server that sent the packet.
     * @param packet
     * the unknown packet.
     * @param channel
     * the channel the packet was sent on.
     */
    fun handleUnknownMessage(
        client: RakNetClient?, peer: RakNetServerPeer?, packet: RakNetPacket?,
        channel: Int
    ) {
    }

    /**
     * Called when the handler receives a packet after the server has already
     * handled it.
     *
     *
     * This method is useful for handling packets outside of the RakNet
     * protocol. All packets received here have already been handled by the
     * client.
     *
     * @param client
     * the client.
     * @param address
     * the address of the sender.
     * @param buf
     * the buffer of the received packet.
     */
    fun handleNettyMessage(client: RakNetClient?, address: InetSocketAddress?, buf: ByteBuf?) {}

    /**
     * Called when a handler exception has occurred.
     *
     *
     * These normally do not matter as long as it does not come from the address
     * of the server the client is connecting to or is connected to.
     *
     * @param client
     * the client.
     * @param address
     * the address that caused the exception.
     * @param throwable
     * the `Throwable` that was caught.
     */
    fun onHandlerException(client: RakNetClient?, address: InetSocketAddress?, throwable: Throwable?) {}

    /**
     * Called when a peer exception has occurred.
     *
     * @param client
     * the client.
     * @param peer
     * the peer that caused the exception to be thrown.
     * @param throwable
     * the `Throwable` that was caught.
     */
    fun onPeerException(client: RakNetClient?, peer: RakNetServerPeer?, throwable: Throwable?) {}
}