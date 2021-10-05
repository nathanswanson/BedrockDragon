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
import java.net.InetSocketAddress
import bedrockDragon.network.raknet.protocol.ConnectionType
import java.net.InetAddress
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import io.netty.buffer.ByteBuf

/**
 * Used to listen for events that occur in the [RakNetServer]. In order to
 * listen for events, one must use the
 * [RakNetServer.addListener] method.
 *
 *
 * Event methods are called on the same thread that called them. Typically, this
 * is the NIO event loop group that the server is using, or the server thread
 * itself. This normally does not matter, however in some cases if a listener
 * takes too long to respond (typically
 * {@value bedrockDragon.network.RakNet.peer.RakNetPeer#PEER_TIMEOUT} milliseconds) then
 * the server can actually timeout.
 *
 *
 * To have event methods called on their own dedicated thread, annotate the
 * listening class with the [ ThreadedListener][bedrockDragon.network.raknet.ThreadedListener] annotation.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
interface RakNetServerListener {
    /**
     * Called when the server has been started.
     *
     * @param server
     * the server.
     */
    fun onStart(server: DragonServer?) {}

    /**
     * Called when the server has been shutdown.
     *
     * @param server
     * the server.
     */
    fun onShutdown(server: DragonServer?) {}

    /**
     * Called when the server receives a ping from a client.
     *
     * @param server
     * the server.
     * @param ping
     * the response that will be sent to the client.
     */
    fun onPing(server: DragonServer?, ping: ServerPing?) {}

    /**
     * Called when a client has connected to the server.
     *
     *
     * This is not the same as [.onLogin],
     * where the client has also completed connection and login.
     *
     * @param server
     * the server.
     * @param address
     * the address of the client.
     * @param connectionType
     * the connection type of the client.
     */
    fun onConnect(server: DragonServer?, address: InetSocketAddress?, connectionType: ConnectionType?) {}

    /**
     * Called when a client has logged in to the server.
     *
     *
     * This is not the same as
     * [.onConnect],
     * where the client has only connected to the server and has not yet logged
     * in.
     *
     * @param server
     * the server.
     * @param peer
     * the client that logged in.
     */
    fun onLogin(server: DragonServer?, peer: RakNetClientPeer?) {}

    /**
     * Called when a client has disconnected from the server.
     *
     * @param server
     * the server.
     * @param address
     * the address of the client that disconnected.
     * @param peer
     * the client that disconnected, this will be `null`
     * if the client has not yet logged in.
     * @param reason
     * the reason the client disconnected.
     */
    fun onDisconnect(
        server: DragonServer?, address: InetSocketAddress?, peer: RakNetClientPeer?,
        reason: String?
    ) {
    }

    /**
     * Called when a client is banned from the server.
     *
     *
     * When a client is banned from the server, they will be actively
     * disconnected with a
     * [ CONNECTION_BANNED][bedrockDragon.network.raknet.protocol.connection.ConnectionBanned] packet. This is different from having an address
     * blocked, as all packets sent from the address will simply be ignored. The
     * server will never automatically ban a client. However, it will
     * automatically block an address if it is suspected of a
     * [DOS](https://en.wikipedia.org/wiki/Denial-of-service_attack)
     * attack.
     *
     * @param server
     * the server.
     * @param address
     * the address of the client.
     * @param reason
     * the reason the client was banned.
     */
    fun onBan(server: DragonServer?, address: InetAddress?, reason: String?) {}

    /**
     * Called when a client is unbanned from the server.
     *
     * @param server
     * the server.
     * @param address
     * the address of the client.
     */
    fun onUnban(server: DragonServer?, address: InetAddress?) {}

    /**
     * Called when an address is blocked by the server.
     *
     *
     * When an address is blocked, all packets sent from it will simply be
     * ignored. This is different from a client being banned, as it will
     * actively be disconnected with a
     * [ CONNECTION_BANNED][bedrockDragon.network.raknet.protocol.connection.ConnectionBanned] packet. The server will never automatically ban a
     * client. However, it will automatically block an address if it is
     * suspected of a
     * [DOS](https://en.wikipedia.org/wiki/Denial-of-service_attack)
     * attack.
     *
     * @param server
     * the server.
     * @param address
     * the address that was blocked.
     * @param reason
     * the reason the address was blocked.
     * @param time
     * how long the address is blocked, with
     * {@value BlockedAddress#PERMANENT_BLOCK} meaning the address is
     * permanently blocked.
     */
    fun onBlock(server: DragonServer?, address: InetAddress?, reason: String?, time: Long) {}

    /**
     * Called when an address has been unblocked by the server.
     *
     * @param server
     * the server.
     * @param address
     * the address that has been unblocked.
     */
    fun onUnblock(server: DragonServer?, address: InetAddress?) {}

    /**
     * Called when a message is acknowledged by a client.
     *
     * @param server
     * the server.
     * @param peer
     * the client that acknwoledged the packet.
     * @param record
     * the acknowledged record.
     * @param packet
     * the acknowledged packet.
     */
    fun onAcknowledge(
        server: DragonServer?, peer: RakNetClientPeer?, record: Record?,
        packet: EncapsulatedPacket?
    ) {
    }

    /**
     * Called when a message is lost by a client.
     *
     * @param server
     * the server.
     * @param peer
     * the client that lost the packet.
     * @param record
     * the lost record.
     * @param packet
     * the lost packet.
     */
    fun onLoss(server: DragonServer?, peer: RakNetClientPeer?, record: Record?, packet: EncapsulatedPacket?) {}

    /**
     * Called when a packet has been received from a client and is ready to be
     * handled.
     *
     * @param server
     * the server.
     * @param peer
     * the client that sent the packet.
     * @param packet
     * the packet received from the client.
     * @param channel
     * the channel the packet was sent on.
     */
    fun handleMessage(server: DragonServer?, peer: RakNetClientPeer?, packet: RakNetPacket?, channel: Int) {}

    /**
     * Called when a packet with an ID below `ID_USER_PACKET_ENUM`
     * cannot be handled by the [RakNetClientPeer] because it is not
     * programmed to handle it.
     *
     *
     * This function can be used to add missing features from the regular RakNet
     * protocol that are absent in JRakNet if needed.
     *
     * @param server
     * the server.
     * @param peer
     * the client that sent the packet.
     * @param packet
     * the unknown packet.
     * @param channel
     * the channel the packet was sent on.
     */
    fun handleUnknownMessage(
        server: DragonServer?, peer: RakNetClientPeer?, packet: RakNetPacket?,
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
     * server.
     *
     * @param server
     * the server.
     * @param address
     * the address of the sender.
     * @param buf
     * the buffer of the received packet.
     */
    fun handleNettyMessage(server: DragonServer?, address: InetSocketAddress?, buf: ByteBuf?) {}

    /**
     * Called when a handler exception has occurred.
     *
     *
     * These normally do not matter as long as the server handles them on its
     * own.
     *
     * @param server
     * the server.
     * @param address
     * the address that caused the exception.
     * @param throwable
     * the `Throwable` that was caught.
     */
    fun onHandlerException(server: DragonServer?, address: InetSocketAddress?, throwable: Throwable?) {}

    /**
     * Called when an exception thrown by a peer has been caught.
     *
     * @param server
     * the server.
     * @param peer
     * the peer that caused the exception.
     * @param throwable
     * the `Throwable` that was caught.
     */
    fun onPeerException(server: DragonServer?, peer: RakNetClientPeer?, throwable: Throwable?) {}
}