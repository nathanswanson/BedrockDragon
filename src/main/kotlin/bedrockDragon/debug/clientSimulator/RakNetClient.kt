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

import bedrockDragon.debug.clientSimulator.peer.PeerFactory
import bedrockDragon.network.raknet.*
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import com.whirvis.jraknet.peer.RakNetServerPeer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Used to connect to servers using the RakNet protocol.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 * @see RakNetClientListener
 *
 * @see .addListener
 * @see .connect
 */
class RakNetClient @JvmOverloads constructor(address: InetSocketAddress? =  /* Solves ambiguity */null as InetSocketAddress?) :
    RakNetClientListener {
    private val bindingAddress: InetSocketAddress?

    /**
     * Returns the client's globally unique ID.
     *
     * @return the client's globally unique ID.
     */
    val globallyUniqueId: Long
    private val timestamp: Long
    private val listeners: ConcurrentLinkedQueue<RakNetClientListener>
    private var eventThreadCount = 0
    private var serverAddress: InetSocketAddress? = null
    private var bootstrap: Bootstrap? = null
    private var handler: RakNetClientHandler? = null
    private var group: EventLoopGroup? = null
    private var channel: Channel? = null
    private var bindAddress: InetSocketAddress? = null
    private lateinit var maximumTransferUnits: Array<MaximumTransferUnit>
    private var highestMaximumTransferUnitSize = 0
    private var peerFactory: PeerFactory? = null

    @Volatile
    private var peer: RakNetServerPeer? = null
    private var peerThread: Thread? = null

    /**
     * Creates a RakNet client.
     *
     * @param address
     * the IP address the client will bind to during connection. A
     * `null` address will have the client bind to the
     * wildcard address.
     * @param port
     * the port the client will bind to during connection. A port of
     * `0` will have the client give Netty the
     * responsibility of choosing the port to bind to.
     */
    constructor(address: InetAddress?, port: Int) : this(InetSocketAddress(address, port)) {}

    /**
     * Creates a RakNet client.
     *
     * @param address
     * the IP address the client will bind to during connection. A
     * `null` address will have the client bind to the
     * wildcard address.
     */
    constructor(address: InetAddress?) : this(InetSocketAddress(address, 0)) {}
    /**
     * Creates a RakNet client.
     *
     * @param host
     * the IP address the client will bind to during connection. A
     * `null` address will have the client bind to the
     * wildcard address.
     * @param port
     * the port the client will bind to during connection. A port of
     * `0` will have the client give Netty the
     * responsibility of choosing the port to bind to.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    /**
     * Creates a RakNet client.
     *
     * @param host
     * the IP address the client will bind to during connection. A
     * `null` address will have the client bind to the
     * wildcard address.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    @JvmOverloads
    constructor(host: String?, port: Int = 0) : this(InetAddress.getByName(host), port) {
    }

    /**
     * Creates a RakNet client.
     *
     * @param port
     * the port the client will bind to during creation. A port of
     * `0` will have the client give Netty the
     * responsibility of choosing the port to bind to.
     */
    constructor(port: Int) : this(InetSocketAddress(port)) {}

    /**
     * Returns the client's networking protocol version.
     *
     * @return the client's networking protocol version.
     */
    val protocolVersion: Int
        get() = RakNet.CLIENT_NETWORK_PROTOCOL

    /**
     * Returns the client's timestamp.
     *
     * @return the client's timestamp.
     */
    fun getTimestamp(): Long {
        return System.currentTimeMillis() - timestamp
    }

    /**
     * Adds a [RakNetClientListener] to the client.
     *
     *
     * Listeners are used to listen for events that occur relating to the client
     * such as connecting to discovers, discovering local servers, and more.
     *
     * @param listener
     * the listener to add.
     * @return the client.
     * @throws NullPointerException
     * if the `listener` is `null`.
     * @throws IllegalArgumentException
     * if the `listener` is another client that is not
     * the client itself.
     */
    @Throws(NullPointerException::class, IllegalArgumentException::class)
    fun addListener(listener: RakNetClientListener?): RakNetClient {
        if (listener == null) {
            throw NullPointerException("Listener cannot be null")
        } else require(!(listener is RakNetClient && this != listener)) { "A client cannot be used as a listener except for itself" }
            if (!listeners.contains(listener)) {
                listeners.add(listener)
                if (listener !== this) {
                } else {
                }
            }
        return this
    }

    /**
     * Adds the client to its own set of listeners, used when extending the
     * [RakNetClient] directly.
     *
     * @return the client.
     * @see RakNetClientListener
     *
     * @see .addListener
     */
    fun addSelfListener(): RakNetClient {
        return addListener(this)
    }

    /**
     * Removes a [RakNetClientListener] from the client.
     *
     * @param listener
     * the listener to remove.
     * @return the client.
     */
    fun removeListener(listener: RakNetClientListener): RakNetClient {
        if (listeners.remove(listener)) {
            if (listener !== this) {
            } else {
            }
        }
        return this
    }

    /**
     * Removes the client from its own set of listeners, used when extending the
     * [RakNetClient] directly.
     *
     * @return the client.
     * @see RakNetClientListener
     *
     * @see .removeListener
     */
    fun removeSelfListener(): RakNetClient {
        return removeListener(this)
    }

    /**
     * Calls an event.
     *
     * @param event
     * the event to call.
     * @throws NullPointerException
     * if the `event` is `null`.
     * @see RakNetClientListener
     */
    @Throws(NullPointerException::class)
    fun callEvent(event: Consumer<in RakNetClientListener>?) {
        if (event == null) {
            throw NullPointerException("Event cannot be null")
        }
        for (listener in listeners) {
            if (listener.javaClass.isAnnotationPresent(ThreadedListener::class.java)) {
                val threadedListener: ThreadedListener = listener.javaClass.getAnnotation(ThreadedListener::class.java)
                object : Thread(
                    RakNetClient::class.java.simpleName + (if (threadedListener.name.length() > 0) "-" else "")
                            + threadedListener.name + "-Thread-" + ++eventThreadCount
                ) {
                    override fun run() {
                        event.accept(listener)
                    }
                }.start()
            } else {
                event.accept(listener)
            }
        }
    }// No channel to check

    /**
     * Returns whether or not the client is currently running.
     *
     *
     * If it is running, this means that it is currently connecting to or is
     * connected to a server.
     *
     * @return `true` if the client is running, `false`
     * otherwise.
     */
    val isRunning: Boolean
        get() = if (channel == null) {
            false // No channel to check
        } else channel!!.isOpen

    /**
     * Returns the address of the server the client is connecting to or is
     * connected to.
     *
     * @return the address of the server the client is connecting to or is
     * connected to, `null` if the client is disconnected.
     */
    fun getServerAddress(): InetSocketAddress? {
        return serverAddress
    }

    /**
     * Returns the address the client is bound to.
     *
     *
     * This will be the value supplied during client creation until the client
     * has connected to a server using the [.connect]
     * method. Once the client has connected a server, the bind address will be
     * changed to the address returned from the channel's
     * [Channel.localAddress] method.
     *
     * @return the address the client is bound to.
     */
    val address: InetSocketAddress?
        get() = bindAddress

    /**
     * Returns the IP address the client is bound to based on the address
     * returned from [.getAddress].
     *
     * @return the IP address the client is bound to.
     */
    val inetAddress: InetAddress
        get() = bindAddress?.getAddress()

    /**
     * Returns the port the client is bound to based on the address returned
     * from [.getAddress].
     *
     * @return the port the client is bound to.
     */
    val port: Int
        get() = bindAddress?.getPort()!!
    /**
     * Returns the maximum transfer unit sizes the client will use during
     * connection.
     *
     * @return the maximum transfer unit sizes the client will use during
     * connection.
     */// Determine valid maximum transfer units

    // Determine the highest maximum transfer unit
    /**
     * Sets the maximum transfer unit sizes that will be used by the client
     * during connection.
     *
     * @param maximumTransferUnitSizes
     * the maximum transfer unit sizes.
     * @throws NullPointerException
     * if the `maximumTransferUnitSizes` is
     * `null`.
     * @throws IllegalArgumentException
     * if the `maximumTransferUnitSizes` is empty or one
     * of its values is less than {@value RakNet#MINIMUM_MTU_SIZE}.
     * @throws RuntimeException
     * if determining the maximum transfer unit for the network card
     * with the client's bind address was a failure or no valid
     * maximum transfer unit could be located for the network card
     * that the client's binding address is bound to.
     */
    @set:Throws(NullPointerException::class, IllegalArgumentException::class, RuntimeException::class)
    var maximumTransferUnitSizes: IntArray
        get() {
            val maximumTransferUnitSizes = IntArray(maximumTransferUnits.size)
            for (i in maximumTransferUnitSizes.indices) {
                maximumTransferUnitSizes[i] = maximumTransferUnits[i].size
            }
            return maximumTransferUnitSizes
        }
        set(maximumTransferUnitSizes) {
            require(maximumTransferUnitSizes.isNotEmpty()) { "At least one maximum transfer unit size must be specified" }

            // Determine valid maximum transfer units
            var foundTransferUnit = false
            val networkCardMaximumTransferUnit: Int = RakNet.getMaximumTransferUnit(bindAddress.getAddress())
            if (networkCardMaximumTransferUnit < 0) {
                throw RuntimeException("Failed to determine maximum transfer unit" + if (bindAddress.getAddress() != null) " for network card with address " + bindAddress.getAddress() else "")
            }
            val maximumTransferUnits = ArrayList<MaximumTransferUnit>()
            for (i in 0 until maximumTransferUnitSizes.size) {
                val maximumTransferUnitSize = maximumTransferUnitSizes[i]
                require(maximumTransferUnitSize >= RakNet.MINIMUM_MTU_SIZE) { "Maximum transfer unit size must be higher than " + RakNet.MINIMUM_MTU_SIZE }
                if (networkCardMaximumTransferUnit >= maximumTransferUnitSize) {
                    maximumTransferUnits.add(
                        MaximumTransferUnit(
                            maximumTransferUnitSize,
                            i * 2 + if (i + 1 < maximumTransferUnitSizes.size) 2 else 1
                        )
                    )
                    foundTransferUnit = true
                }
            }
            this.maximumTransferUnits = maximumTransferUnits.toTypedArray()

            // Determine the highest maximum transfer unit
            var highestMaximumTransferUnit = Int.MIN_VALUE
            for (maximumTransferUnit in maximumTransferUnits) {
                if (maximumTransferUnit.size > highestMaximumTransferUnit) {
                    highestMaximumTransferUnit = maximumTransferUnit.size
                }
            }
            highestMaximumTransferUnitSize = highestMaximumTransferUnit
            if (foundTransferUnit == false) {
                throw RuntimeException("No compatible maximum transfer unit found for machine network cards")
            }
            val registeredMaximumTransferUnitSizes = IntArray(maximumTransferUnits.size)
            for (i in registeredMaximumTransferUnitSizes.indices) {
                registeredMaximumTransferUnitSizes[i] = this.maximumTransferUnits[i].size
            }
            val registeredMaximumTransferUnitSizesStr = Arrays.toString(registeredMaximumTransferUnitSizes)

        }

    /**
     * Returns the peer of the server the client is currently connected to.
     *
     * @return the peer of the server the client is currently connected to,
     * `null` if it is not connected to a server.
     */
    val server: RakNetServerPeer?
        get() = peer// No peer

    /**
     * Returns whether or not the client is connected.
     *
     *
     * The client is considered connected if the current state is
     * [RakNetState.CONNECTED] or has a higher order. This does not apply
     * to the [.isHandshaking], [.isLoggedIn], or
     * [.isDisconnected] methods.
     *
     * @return `true` if the client is connected, `false`
     * otherwise.
     */
    val isConnected: Boolean
        get() = if (peer == null) {
            false // No peer
        } else peer.isConnected()// No peer

    /**
     * Returns whether or not the client is handshaking.
     *
     * @return `true` if the client is handshaking,
     * `false` otherwise.
     */
    val isHandshaking: Boolean
        get() = if (peer == null) {
            false // No peer
        } else peer.isHandshaking()// No peer

    /**
     * Returns whether or not the client is logged in.
     *
     * @return `true` if the client is logged in, `false`
     * otherwise.
     */
    val isLoggedIn: Boolean
        get() = if (peer == null) {
            false // No peer
        } else peer.isLoggedIn()// No peer

    /**
     * Returns whether or not the client is disconnected.
     *
     * @return `true` if the client is disconnected,
     * `false` otherwise.
     */
    val isDisconnected: Boolean
        get() = if (peer == null) {
            true // No peer
        } else peer.isDisconnected()

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException
     * if the client is not connected to a server.
     */
    @Throws(IllegalStateException::class)
    fun sendMessage(reliability: Reliability?, channel: Int, packet: Packet?): EncapsulatedPacket {
        check(isConnected) { "Cannot send messages while not connected to a server" }
        return peer.sendMessage(reliability, channel, packet)
    }

    /**
     * Sends a Netty message over the channel raw.
     *
     *
     * This should be used sparingly, as if it is used incorrectly it could
     * break server peers entirely. In order to send a message to a peer, use
     * one of the
     * [ sendMessage()][com.whirvis.jraknet.peer.RakNetPeer.sendMessage] methods.
     *
     * @param buf
     * the buffer to send.
     * @param address
     * the address to send the buffer to.
     * @throws NullPointerException
     * if the `buf`, `address`, or IP address
     * of `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun sendNettyMessage(buf: ByteBuf?, address: InetSocketAddress?) {
        if (buf == null) {
            throw NullPointerException("Buffer cannot be null")
        } else if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.getAddress() == null) {
            throw NullPointerException("IP address cannot be null")
        }
        channel!!.writeAndFlush(DatagramPacket(buf, address))

    }

    /**
     * Sends a Netty message over the channel raw.
     *
     *
     * This should be used sparingly, as if it is used incorrectly it could
     * break server peers entirely. In order to send a message to a peer, use
     * one of the
     * [ sendMessage()][com.whirvis.jraknet.peer.RakNetPeer.sendMessage] methods.
     *
     * @param packet
     * the packet to send.
     * @param address
     * the address to send the packet to.
     * @throws NullPointerException
     * if the `packet`, `address`, or IP
     * address of `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun sendNettyMessage(packet: Packet?, address: InetSocketAddress?) {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        this.sendNettyMessage(packet.buffer(), address)
    }

    /**
     * Sends a Netty message over the channel raw.
     *
     *
     * This should be used sparingly, as if it is used incorrectly it could
     * break server peers entirely. In order to send a message to a peer, use
     * one of the
     * [ sendMessage()][com.whirvis.jraknet.peer.RakNetPeer.sendMessage] methods.
     *
     * @param packetId
     * the packet ID to send.
     * @param address
     * the address to send the packet to.
     * @throws NullPointerException
     * if the `address` or IP address of
     * `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun sendNettyMessage(packetId: Int, address: InetSocketAddress?) {
        this.sendNettyMessage(RakNetPacket(packetId), address)
    }

    /**
     * Handles a packet received by the [RakNetClientHandler].
     *
     * @param sender
     * the address of the sender.
     * @param packet
     * the packet to handle.
     * @throws NullPointerException
     * if the `sender` or `packet` are
     * `null`.
     */
    @Throws(NullPointerException::class)
    fun handleMessage(sender: InetSocketAddress?, packet: RakNetPacket?) {
        if (sender == null) {
            throw NullPointerException("Sender cannot be null")
        } else if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        } else if (peerFactory != null) {
            if (sender == peerFactory.getAddress()) {
                val peer: RakNetServerPeer = peerFactory.assemble(packet)
                if (peer != null) {
                    this.peer = peer
                    peerFactory = null
                }
            }
        } else if (peer != null) {
            peer.handleInternal(packet)
        }
    }

    /**
     * Called by the [ RakNetClientHander][com.whirvis.jraknet.client.RakNetClientHandler] when it catches a `Throwable` while
     * handling a packet.
     *
     * @param address
     * the address that caused the exception.
     * @param cause
     * the `Throwable` caught by the handler.
     * @throws NullPointerException
     * if the cause `address` or `cause` are
     * `null`.
     */
    @Throws(NullPointerException::class)
    fun handleHandlerException(address: InetSocketAddress?, cause: Throwable?) {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (cause == null) {
            throw NullPointerException("Cause cannot be null")
        } else if (peerFactory != null) {
            if (address == peerFactory.getAddress()) {
                peerFactory.exceptionCaught(NettyHandlerException(this, handler, address, cause))
            }
        } else if (peer != null) {
            if (address == peer.getAddress()) {
                this.disconnect(cause)
            }
        }
        callEvent { listener: RakNetClientListener -> listener.onHandlerException(this, address, cause) }
    }

    /**
     * Connects the client to a server.
     *
     * @param address
     * the address of the server to connect to.
     * @throws NullPointerException
     * if the `address` or the IP address of the
     * `address` is `null`.
     * @throws IllegalStateException
     * if the client is currently connected to a server.
     * @throws RakNetException
     * if an error occurs during connection or login.
     */
    @Throws(NullPointerException::class, IllegalStateException::class, RakNetException::class)
    fun connect(address: InetSocketAddress?) {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.getAddress() == null) {
            throw NullPointerException("IP address cannot be null")
        } else check(!isConnected) { "Client is currently connected to a server" }

        // Initiate networking
        serverAddress = address
        try {
            bootstrap = Bootstrap()
            group = NioEventLoopGroup()
            handler = RakNetClientHandler(this)
            bootstrap!!.channel(NioDatagramChannel::class.java).group(group).handler(handler)
            bootstrap!!.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, false)
            channel = (if (bindingAddress != null) bootstrap.bind(bindingAddress) else bootstrap!!.bind(0)).sync()
                .channel()
            bindAddress = channel.localAddress() as InetSocketAddress
            maximumTransferUnitSizes = * RakNetClient . Companion . DEFAULT_TRANSFER_UNIT_SIZES

        } catch (e: InterruptedException) {
            throw RakNetException(e)
        }

        // Prepare connection
        val units: Array<MaximumTransferUnit?> = MaximumTransferUnit.Companion.sort(*maximumTransferUnits)
        for (unit in maximumTransferUnits) {
            unit.reset()
        }
        peerFactory = PeerFactory(
            this, address, bootstrap, channel, units[0].getSize(),
            highestMaximumTransferUnitSize
        )
        peerFactory.startAssembly(units)

        // Send connection packet
        val connectionRequest = ConnectionRequest()
        connectionRequest.clientGuid = globallyUniqueId
        connectionRequest.timestamp = System.currentTimeMillis() - timestamp
        connectionRequest.encode()
        peer.sendMessage(Reliability.RELIABLE_ORDERED, connectionRequest)

        // Create and start peer update thread
        val client = this
        peerThread = object : Thread(
            RakNetClient::class.java.simpleName + "-Peer-Thread-" + java.lang.Long.toHexString(globallyUniqueId)
                .toUpperCase()
        ) {
            override fun run() {
                while (peer != null && !this.isInterrupted) {
                    try {
                        sleep(0, 1) // Lower CPU usage
                    } catch (e: InterruptedException) {
                        interrupt() // Interrupted during sleep
                        continue
                    }
                    if (peer != null) {
                        if (!peer.isDisconnected()) {
                            try {
                                peer.update()
                            } catch (throwable: Throwable) {
                                client.callEvent { listener: RakNetClientListener ->
                                    listener.onPeerException(
                                        client,
                                        peer,
                                        throwable
                                    )
                                }
                                if (!peer.isDisconnected()) {
                                    client.disconnect(throwable)
                                }
                            }
                        }
                    }
                }
            }
        }
        peerThread.start()
    }

    /**
     * Connects the client to a server.
     *
     * @param address
     * the IP address of the server to connect to.
     * @param port
     * the port of the server to connect to.
     * @throws NullPointerException
     * if the `address` is `null`.
     * @throws IllegalArgumentException
     * if the `port` is not in between
     * `0-65535`.
     * @throws IllegalStateException
     * if the client is currently connected to a server.
     * @throws RakNetException
     * if an error occurs during connection or login.
     */
    @Throws(
        NullPointerException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        RakNetException::class
    )
    fun connect(address: InetAddress?, port: Int) {
        if (address == null) {
            throw NullPointerException("IP address cannot be null")
        } else require(!(port < 0x0000 || port > 0xFFFF)) { "Port must be in between 0-65535" }
        this.connect(InetSocketAddress(address, port))
    }

    /**
     * Connects the client to a server.
     *
     * @param host
     * the IP address of the server to connect to.
     * @param port
     * the port of the server to connect to.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws IllegalArgumentException
     * if the `port` is not within the range of
     * `0-65535`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     * @throws IllegalStateException
     * if the client is currently connected to a server.
     * @throws RakNetException
     * if an error occurs during connection or login.
     */
    @Throws(
        NullPointerException::class,
        IllegalArgumentException::class,
        UnknownHostException::class,
        IllegalStateException::class,
        RakNetException::class
    )
    fun connect(host: String?, port: Int) {
        if (host == null) {
            throw NullPointerException("IP address cannot be null")
        }
        this.connect(InetAddress.getByName(host), port)
    }

    /**
     * Disconnects the client from the server.
     *
     * @param reason
     * the reason for disconnection. A `null` reason will
     * have `"Disconnected"` be used as the reason
     * instead.
     * @throws IllegalStateException
     * if the client is not connected to a server.
     */
    /**
     * Disconnects the client from the server.
     *
     * @throws IllegalStateException
     * if the client is not connected to a server.
     */
    @JvmOverloads
    @Throws(IllegalStateException::class)
    fun disconnect(reason: String? =  /* Solves ambiguity */null as String?) {
        checkNotNull(peer) { "Client is not connected to a server" }

        // Disconnect peer and interrupt thread
        peerThread!!.interrupt()
        peerThread = null
        val peer: RakNetServerPeer? = peer
        if (!peer.isDisconnected()) {
            peer.disconnect()
            this.peer = null
        }

        callEvent { listener: RakNetClientListener ->
            listener.onDisconnect(
                this,
                serverAddress,
                peer,
                reason ?: "Disconnected"
            )
        }

        // Shutdown networking
        channel!!.close()
        group?.shutdownGracefully(0L, 1000L, TimeUnit.MILLISECONDS)
        serverAddress = null
        channel = null
        handler = null
        group = null
        bootstrap = null
    }

    /**
     * Disconnects the client from the server.
     *
     * @param reason
     * the reason for disconnection. A `null` reason will
     * have `"Disconnected"` be used as the reason
     * instead.
     * @throws IllegalStateException
     * if the client is not connected to a server.
     */
    @Throws(IllegalStateException::class)
    fun disconnect(reason: Throwable?) {
        this.disconnect(if (reason != null) RakNet.getStackTrace(reason) else null)
    }

    override fun toString(): String {
        return ("RakNetClient [bindingAddress=" + bindingAddress + ", guid=" + globallyUniqueId + ", timestamp=" + timestamp
                + ", bindAddress=" + bindAddress + ", maximumTransferUnits=" + Arrays.toString(maximumTransferUnits)
                + ", highestMaximumTransferUnitSize=" + highestMaximumTransferUnitSize + ", getProtocolVersion()="
                + protocolVersion + ", getTimestamp()=" + getTimestamp() + ", getAddress()=" + address
                + ", isConnected()=" + isConnected + "]")
    }

    companion object {
        /**
         * The default maximum transfer unit sizes used by the client.
         *
         *
         * These were chosen due to the maximum transfer unit sizes used by the
         * Minecraft client during connection.
         */
        val DEFAULT_TRANSFER_UNIT_SIZES = intArrayOf(1492, 1200, 576, RakNet.MINIMUM_MTU_SIZE)

        /**
         * The amount of time to wait before the client broadcasts another ping to
         * the local network and all added external servers.
         *
         *
         * This was also determined based on Minecraft's frequency of broadcasting
         * pings to servers.
         */
        const val PING_BROADCAST_WAIT_MILLIS = 1000L
    }
    /**
     * Creates a RakNet client.
     *
     * @param address
     * the address the client will bind to during connection. A
     * `null` address will have the client bind to the
     * wildcard address along with the client giving Netty the
     * responsibility of choosing which port to bind to.
     */
    /**
     * Creates a RakNet client.
     */
    init {
        bindingAddress = address
        globallyUniqueId = UUID.randomUUID().getMostSignificantBits()
        timestamp = System.currentTimeMillis()
        listeners = ConcurrentLinkedQueue<RakNetClientListener>()
        if (this.javaClass != RakNetClient::class.java && RakNetClientListener::class.java.isAssignableFrom(this.javaClass)) {
            addSelfListener()
        }
    }
}