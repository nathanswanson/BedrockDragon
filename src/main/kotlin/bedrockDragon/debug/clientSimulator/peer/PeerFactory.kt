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

import bedrockDragon.debug.clientSimulator.MaximumTransferUnit
import bedrockDragon.debug.clientSimulator.RakNetClient
import bedrockDragon.network.raknet.PacketBufferException
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.connection.*
import bedrockDragon.debug.clientSimulator.RakNetServerPeer
import bedrockDragon.network.raknet.handler.PacketConstants
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.RecvByteBufAllocator
import java.net.InetSocketAddress

/**
 * Used by the [RakNetClient] to create a [RakNetServerPeer].
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.0.0
 */
class PeerFactory(
    client: RakNetClient?, address: InetSocketAddress, bootstrap: Bootstrap, channel: Channel,
    initialMaximumTransferUnit: Int, maximumMaximumTransferUnit: Int
) {
    private var factoryState: Int
    private val client: RakNetClient?
    private val address: InetSocketAddress
    private val bootstrap: Bootstrap
    private val channel: Channel
    private val initialMaximumTransferUnit: Int
    private val maximumMaximumTransferUnit: Int
    private var throwable: Throwable? = null
    private var serverGuid: Long = 0
    private var maximumTransferUnit = 0
    private var connectionType: ConnectionType? = null

    /**
     * Returns the address of the server that the peer is being assembled for.
     *
     * @return the address of the server that the peer is being assembled for.
     */
    fun getAddress(): InetSocketAddress {
        return address
    }

    /**
     * Called when an exception is caused by the server that the peer is being
     * assembled for. This will cause the
     * [.startAssembly] method to throw the
     * `Throwable` specified here.
     *
     * @param throwable
     * the `Throwable` the server caused to be thrown.
     * @throws NullPointerException
     * if the `throwable` is `null`.
     * @throws IllegalStateException
     * if the peer is not currently being assembled, or has already
     * been assembled.
     */
    @Throws(NullPointerException::class, IllegalStateException::class)
    fun exceptionCaught(throwable: Throwable?) {
        if (throwable == null) {
            throw NullPointerException("Throwable cannot be null")
        } else check(factoryState > STATE_IDLE) { "Peer is not currently being assembled" }
            check(factoryState < STATE_PEER_ASSEMBLED) { "Peer has already been assembled" }
        this.throwable = throwable
    }

    /**
     * Starts peer assembly.
     *
     *
     * This will block the thread. However, packets will still be received by
     * Netty. When a packet has been received, it should be sent back to the
     * factory using the [.assemble] method.
     *
     * @param units
     * the maximum transfer units the client will attempt to use with
     * the server.
     * @throws NullPointerException
     * if the `units` are `null`.
     * @throws IllegalStateException
     * if the peer has already been assembled or is currently being
     * assembled.
     * @throws PeerFactoryException
     * if an error occurs when assembling the peer. It is possible
     * for this method to throw a `PeerFactoryException`
     * through the [.exceptionCaught] method. Any
     * exception caught in the [.assemble]
     * method will be routed back and thrown here through this
     * method.
     * @throws PacketBufferException
     * if encoding or decoding one of the packets fails.
     */
    @Throws(
        NullPointerException::class,
        IllegalStateException::class,
        PeerFactoryException::class,
        PacketBufferException::class
    )
    fun startAssembly(vararg units: MaximumTransferUnit?) {
        if (units == null) {
            throw NullPointerException("Maximum transfer units cannot be null")
        } else check(factoryState < STATE_PEER_ASSEMBLED) { "Peer has already been assembled" }
        check(factoryState <= STATE_IDLE) { "Peer is already being assembled" }
        factoryState = STATE_FIRST_CONNECTION_REQUEST

        // Send open connection request one with a decreasing MTU
        var availableAttempts = 0
        for (unit in units) {
            availableAttempts += unit!!.retries
            if (unit != null) {
                while (unit.retry() > 0 && factoryState < STATE_SECOND_CONNECTION_REQUEST && throwable == null) {
                    val connectionRequestOne = OpenConnectionRequestOne()
                    connectionRequestOne.maximumTransferUnit = unit.size
                    connectionRequestOne.networkProtocol = client!!.protocolVersion
                    connectionRequestOne.encode()
                    client?.sendNettyMessage(connectionRequestOne, address)
                    RakNet.sleep(500)
                }
            }
        }

        // If the state did not update then the server is offline
        if (factoryState < STATE_SECOND_CONNECTION_REQUEST && throwable == null) {
            throw ServerOfflineException(client, address)
        }

        // Send open connection request two until a response is received
        while (availableAttempts-- > 0 && factoryState < STATE_PEER_ASSEMBLED && throwable == null) {
            val connectionRequestTwo = OpenConnectionRequestTwo()
            connectionRequestTwo.clientGuid = client!!.globallyUniqueId
            connectionRequestTwo.serverAddress = address
            connectionRequestTwo.maximumTransferUnit = maximumTransferUnit
            connectionRequestTwo.encode()
            if (!connectionRequestTwo.failed()) {
                client?.sendNettyMessage(connectionRequestTwo, address)

                RakNet.sleep(500)
            } else {
                throw PacketBufferException(connectionRequestTwo)
            }
        }

        // If the state did not update then the server has gone offline
        if (factoryState < STATE_PEER_ASSEMBLED && throwable == null) {
            throw ServerOfflineException(client, address)
        } else if (throwable != null) {
            if (throwable is PeerFactoryException) {
                throw (throwable as PeerFactoryException?)!!
            } else if (throwable is PacketBufferException) {
                throw (throwable as PacketBufferException?)!!
            } else {
                throw PeerFactoryException(client, throwable)
            }
        }
    }

    /**
     * Further assembles the peer creation by handling the specified packet.
     *
     * @param packet
     * the packet to handle.
     * @return the created peer, `null` if the peer is not yet
     * finished assembling.
     * @throws NullPointerException
     * if the `packet` is `null`.
     * @throws IllegalStateException
     * if the peer is not currently being assembled or if the peer
     * has already been assembled.
     */
    @Throws(NullPointerException::class, IllegalStateException::class)
    fun assemble(packet: RakNetPacket?): RakNetServerPeer? {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        } else if (factoryState <= STATE_IDLE) {
            throw IllegalStateException("Peer is not currently being assembled")
        } else // Create pee
            /*
        * If the maximum transfer unit of the server is smaller
        * than that of the client, then use that one. Otherwise,
        * use the highest valid maximum transfer unit of the
        * client.
        */
            kotlin.check(factoryState < PeerFactory.Companion.STATE_PEER_ASSEMBLED) { "Peer has already been assembled" }
        try {
            if (packet.id.toInt() == PacketConstants.SERVER_TO_CLIENT_HANDSHAKE_1 && factoryState == STATE_FIRST_CONNECTION_REQUEST) {
                val connectionResponseOne = OpenConnectionResponseOne(packet)
                connectionResponseOne.decode()
                if (connectionResponseOne.magic === false) {
                    throw InvalidMagicException(client)
                } else if (connectionResponseOne.maximumTransferUnit < RakNet.MINIMUM_MTU_SIZE) {
                    throw InvalidMaximumTransferUnitException(
                        client,
                        connectionResponseOne.maximumTransferUnit
                    )
                }

                /*
					 * If the maximum transfer unit of the server is smaller
					 * than that of the client, then use that one. Otherwise,
					 * use the highest valid maximum transfer unit of the
					 * client.
					 */maximumTransferUnit = Math.min(
                    connectionResponseOne.maximumTransferUnit,
                    maximumMaximumTransferUnit
                )
                serverGuid = connectionResponseOne.serverGuid
                factoryState = STATE_SECOND_CONNECTION_REQUEST

            } else if (packet.id.toInt() == PacketConstants.SERVER_TO_CLIENT_HANDSHAKE_2
                && factoryState == STATE_SECOND_CONNECTION_REQUEST
            ) {
                val connectionResponseTwo = OpenConnectionResponseTwo(packet)
                connectionResponseTwo.decode()
                if (connectionResponseTwo.failed()) {
                    throw PacketBufferException(connectionResponseTwo)
                } else if (connectionResponseTwo.magic === false) {
                    throw InvalidMagicException(client)
                } else if (connectionResponseTwo.serverGuid !== serverGuid) {
                    throw InconsistentGuidException(client)
                } else if (connectionResponseTwo.maximumTransferUnit > maximumMaximumTransferUnit
                    || connectionResponseTwo.maximumTransferUnit < RakNet.MINIMUM_MTU_SIZE
                ) {
                    throw InvalidMaximumTransferUnitException(client, maximumTransferUnit)
                } else if (connectionResponseTwo.maximumTransferUnit > maximumTransferUnit) {
                }
                bootstrap.option(ChannelOption.SO_SNDBUF, maximumTransferUnit)
                    .option(ChannelOption.SO_RCVBUF, maximumTransferUnit)
                    .option<RecvByteBufAllocator>(
                        ChannelOption.RCVBUF_ALLOCATOR,
                        FixedRecvByteBufAllocator(maximumTransferUnit)
                    )

                // Create peer
                maximumTransferUnit = connectionResponseTwo.maximumTransferUnit
                connectionType = connectionResponseTwo.connectionType
                factoryState = STATE_PEER_ASSEMBLED
                client?.callEvent { listener -> listener.onConnect(client, address, connectionType) }

                return client?.let {
                    connectionType?.let { it1 ->
                        RakNetServerPeer(
                            it, address, serverGuid, maximumTransferUnit, it1,
                            channel
                        )
                    }
                }
            } else if (packet.id.toInt() == PacketConstants.ALREADY_CONNECTED) {
                throw AlreadyConnectedException(client, address)
            } else if (packet.id.toInt() == PacketConstants.NO_FREE_INCOMING_CONNECTIONS) {
                throw NoFreeIncomingConnectionsException(client, address)
            } else if (packet.id.toInt() == PacketConstants.CONNECTION_BANNED) {
                val connectionBanned = ConnectionBanned(packet)
                connectionBanned.decode()
                if (connectionBanned.magic !== true) {
                    throw InvalidMagicException(client)
                } else if (connectionBanned.serverGuid === serverGuid) {
                    throw ConnectionBannedException(client, address)
                }
            } else if (packet.id.toInt() == PacketConstants.INCOMPATIBLE_PROTOCOL) {
                val incompatibleProtocol = IncompatibleProtocolVersion(packet)
                incompatibleProtocol.decode()
                if (incompatibleProtocol.serverGuid === serverGuid) {
                    throw IncompatibleProtocolException(
                        client, address, client!!.protocolVersion,
                        incompatibleProtocol.networkProtocol
                    )
                }
            }
        } catch (e: PeerFactoryException) {
            exceptionCaught(e)
        } catch (e: PacketBufferException) {
            exceptionCaught(e)
        }
        return null
    }

    override fun toString(): String {
        return ("PeerFactory [factoryState=" + factoryState + ", client=" + client + ", address=" + address
                + ", initialMaximumTransferUnit=" + initialMaximumTransferUnit + ", maximumMaximumTransferUnit="
                + maximumMaximumTransferUnit + ", serverGuid=" + serverGuid + ", maximumTransferUnit="
                + maximumTransferUnit + ", connectionType=" + connectionType + "]")
    }

    companion object {
        /**
         * The factory is not currently in the process of doing anything.
         */
        private const val STATE_IDLE = -1

        /**
         * The factory needs to send a [ OPEN_CONNECTION_REQUEST_1][OpenConnectionRequestOne] packet and get a
         * [OPEN_CONNECTION_RESPONSE_1][OpenConnectionRequestTwo] packet in
         * order to proceed to the next state.
         */
        private const val STATE_FIRST_CONNECTION_REQUEST = 0

        /**
         * The factory needs to send a [ OPEN_CONNECTION_REQUEST_2][OpenConnectionRequestTwo] packet and get a
         * [OPEN_CONNECTION_RESPONSE_2][OpenConnectionResponseTwo] packet in
         * response to finish peer creation.
         */
        private const val STATE_SECOND_CONNECTION_REQUEST = 1

        /**
         * The peer has been assembled.
         */
        private const val STATE_PEER_ASSEMBLED = 2
    }

    /**
     * Creates a peer factory.
     *
     * @param client
     * the client connecting to the server.
     * @param address
     * the address of the server.
     * @param bootstrap
     * the bootstrap the `channel` belongs to. Once a
     * maximum transfer unit has been decided upon, its
     * [ChannelOption.SO_SNDBUF] and
     * [ChannelOption.SO_RCVBUF] will be set to it.
     * @param channel
     * the channel to use when creating the peer.
     * @param initialMaximumTransferUnit
     * the initial maximum transfer unit size.
     * @param maximumMaximumTransferUnit
     * the maximum transfer unit with the highest size.
     * @throws NullPointerException
     * if the `client`, `address` or IP
     * address are `null`.
     */
    init {
        if (client == null) {
            throw NullPointerException("Client cannot be null")
        } else if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.getAddress() == null) {
            throw NullPointerException("IP address cannot be null")
        }
        factoryState = STATE_IDLE
        this.client = client
        this.address = address
        this.bootstrap = bootstrap
        this.channel = channel
        this.initialMaximumTransferUnit = initialMaximumTransferUnit
        this.maximumMaximumTransferUnit = maximumMaximumTransferUnit
    }
}