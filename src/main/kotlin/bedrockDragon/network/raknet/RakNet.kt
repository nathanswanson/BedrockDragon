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
package bedrockDragon.network.raknet


import bedrockDragon.network.raknet.handler.PacketConstants
import java.util.UUID
import java.util.HashMap

import java.lang.InterruptedException
import kotlin.Throws
import java.lang.NullPointerException
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.jvm.Synchronized
import java.lang.IllegalArgumentException
import bedrockDragon.network.upnp.UPnP
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.channel.ChannelOption
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestOne
import bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseOne
import bedrockDragon.network.raknet.protocol.connection.IncompatibleProtocolVersion
import java.lang.RuntimeException
import io.netty.bootstrap.Bootstrap
import java.lang.NumberFormatException
import kotlin.jvm.JvmOverloads
import java.lang.Runnable
import java.lang.IllegalStateException
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlin.jvm.Volatile
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import java.lang.Exception
import java.net.*

/**
 * The main RakNet component class, containing protocol information and utility
 * methods.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
object RakNet {
    //private static final Logger LOGGER = LogManager.getLogger(RakNet.class);
    /**
     * The length of IPv4 addresses.
     */
    const val IPV4_ADDRESS_LENGTH = 4

    /**
     * The length of IPv6 addresses.
     */
    const val IPV6_ADDRESS_LENGTH = 16

    /**
     * The IPv4 version.
     */
    const val IPV4 = 4

    /**
     * The IPv6 version.
     */
    const val IPV6 = 6

    /**
     * The Inet 6 address family.
     */
    const val AF_INET6 = 10

    /**
     * The amount of times the [.isServerOnline] and
     * [.isServerCompatible] methods will attempt to
     * ping the server before giving up.
     */
    const val PING_RETRIES = 5

    /**
     * The timestamp the [.isServerOnline] and
     * [.isServerCompatible] methods will use as the
     * ping timestamp.
     */
    val PING_TIMESTAMP = System.currentTimeMillis()

    /**
     * The ping ID that the [.isServerOnline] and
     * [.isServerCompatible] methods will use as the
     * ping ID.
     */
    val PING_ID = UUID.randomUUID().leastSignificantBits

    /**
     * The amount of times the [.getServerIdentifier]
     * method will attempt to retrieve the server identifier before giving up.
     */
    const val IDENTIFIER_RETRIES = 3

    /**
     * The current supported server network protocol.
     */
    const val SERVER_NETWORK_PROTOCOL = 10

    /**
     * The current supported client network protocol.
     */
    const val CLIENT_NETWORK_PROTOCOL = 10

    /**
     * The minimum maximum transfer unit size.
     */
    const val MINIMUM_MTU_SIZE = 400

    /**
     * The amount of available channels there are to send packets on.
     */
    const val CHANNEL_COUNT = 32

    /**
     * The default channel packets are sent on.
     */
    const val DEFAULT_CHANNEL: Byte = 0

    /**
     * The amount of time in milliseconds an address will be blocked if it sends
     * too many packets in one second.
     */
    const val MAX_PACKETS_PER_SECOND_BLOCK = 300000L

    /**
     * The default system address used when one is left unspecified.
     */
	@JvmField
	val SYSTEM_ADDRESS = InetSocketAddress("0.0.0.0", 0)

    /**
     * The default of amount of system address used by RakNet.
     */
    const val RAKNET_SYSTEM_ADDRESS_COUNT = 10

    /**
     * The amount of system addresses used by Minecraft.
     */
    const val MINECRAFT_SYSTEM_ADDRESS_COUNT = 20
    private val MAXIMUM_TRANSFER_UNIT_SIZES = HashMap<InetAddress, Int>()
    private var _lowestMaximumTransferUnitSize = -1
    private var _maxPacketsPerSecond: Long = 500
    private var _systemAddressCount = RAKNET_SYSTEM_ADDRESS_COUNT

    /**
     * Sleeps the current thread for the specified amount of time in
     * milliseconds.
     *
     *
     * If an `InterruptedException` is caught during the sleep,
     * `Thread.currentThread().interrupt()` will automatically be
     * called.
     *
     * @param time
     * the amount of time the thread should sleep in milliseconds.
     * @return `true` if the current thread was interrupted during
     * the sleep, `false` otherwise.
     */
	@JvmStatic
	fun sleep(time: Long): Boolean {
        return try {
            Thread.sleep(time)
            false
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            true
        }
    }

    /**
     * Returns the version of the specified IP address.
     *
     * @param address
     * the IP address.
     * @return the version of the IP address, `-1` if the version is
     * unknown.
     * @throws NullPointerException
     * if the `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun getAddressVersion(address: InetAddress?): Int {
        if (address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        val length = address.address.size
        if (length == IPV4_ADDRESS_LENGTH) {
            return IPV4
        } else if (length == IPV6_ADDRESS_LENGTH) {
            return IPV6
        }
        return -1
    }

    /**
     * Returns the version of the IP address of the specified address.
     *
     * @param address
     * the address.
     * @return the version of the IP address, `-1` if the version is
     * unknown.
     * @throws NullPointerException
     * if the `address` or the IP of the
     * `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun getAddressVersion(address: InetSocketAddress?): Int {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        }
        return getAddressVersion(address.address)
    }

    /**
     * Returns whether or not the specified IP address is a local address.
     *
     * @param address
     * the IP address.
     * @return `true` if the address is a local address,
     * `false` otherwise.
     * @throws NullPointerException
     * if the `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun isLocalAddress(address: InetAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("IP address cannot be null")
        } else if (address.isAnyLocalAddress || address.isLoopbackAddress) {
            return true
        }
        return try {
            NetworkInterface.getByInetAddress(address) != null
        } catch (e: SocketException) {
            false
        }
    }

    /**
     * Returns whether or not the specified IP address is a local address.
     *
     * @param host
     * the IP address.
     * @return `true` if the address is a local address,
     * `false` otherwise.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    @Throws(NullPointerException::class, UnknownHostException::class)
    fun isLocalAddress(host: String?): Boolean {
        if (host == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isLocalAddress(InetAddress.getByName(host))
    }

    /**
     * Returns whether or not the specified address is a local address.
     *
     * @param address
     * the address.
     * @return `true` if the address is a local address,
     * `false` otherwise.
     * @throws NullPointerException
     * if the `address` or the IP address of the
     * `address` are `null`.
     */
    @JvmStatic
	@Throws(NullPointerException::class)
    fun isLocalAddress(address: InetSocketAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isLocalAddress(address.address)
    }

    /**
     * Returns whether or not the specified address is an address belonging to
     * this machine.
     *
     * @param address
     * the address.
     * @return `true` if the address is an address belonging to this
     * machine, `false` otherwise.
     * @throws NullPointerException
     * if the `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun isSystemAddress(address: InetAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        }
        return try {
            NetworkInterface.getByInetAddress(address) != null
        } catch (e: SocketException) {
            false
        }
    }

    /**
     * Returns whether or not the specified IP address is an address belonging
     * to this machine.
     *
     * @param host
     * the IP address.
     * @return `true` if the address is is an address belonging to
     * this machine, `false` otherwise.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    @Throws(NullPointerException::class, UnknownHostException::class)
    fun isSystemAddress(host: String?): Boolean {
        if (host == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isSystemAddress(InetAddress.getByName(host))
    }

    /**
     * Returns whether or not the specified address is an address belonging to
     * this machine.
     *
     * @param address
     * the address.
     * @return `true` if the address is an address belonging to this
     * machine, `false` otherwise.
     * @throws NullPointerException
     * if the `address` or the IP address of the
     * `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun isSystemAddress(address: InetSocketAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isSystemAddress(address.address)
    }

    /**
     * Converts the stack trace of the specified `Throwable` to a
     * string.
     *
     * @param throwable
     * the `Throwable` to get the stack trace from.
     * @return the stack trace as a string.
     * @throws NullPointerException
     * if the `throwable` is `null`.
     */
    @JvmStatic
	@Throws(NullPointerException::class)
    fun getStackTrace(throwable: Throwable?): String {
        if (throwable == null) {
            throw NullPointerException("Throwable cannot be null")
        }
        val stackTraceOut = ByteArrayOutputStream()
        val stackTracePrint = PrintStream(stackTraceOut)
        throwable.printStackTrace(stackTracePrint)
        var printedStackTrace = String(stackTraceOut.toByteArray())
        if (printedStackTrace.endsWith("\n")) {
            printedStackTrace = printedStackTrace.substring(0, printedStackTrace.length - 2)
        }
        return printedStackTrace
    }

    /**
     * Forwards the specified UDP port via
     * [UPnP](https://en.wikipedia.org/wiki/Universal_Plug_and_Play).
     *
     *
     * In order for this method to work,
     * [UPnP](https://en.wikipedia.org/wiki/Universal_Plug_and_Play)
     * for the router must be enabled. The way to enable this varies depending
     * on the router. There is no guarantee this method will successfully
     * forward the specified UDP port; as it is completely dependent on the
     * gateway (the router in this case) to do so.
     *
     *
     * This is not a blocking method. However, the code required to accomplish
     * the task can up to three seconds to execute. As a result, it is
     * encapsulated within another thread so as to prevent unnecessary blocking.
     * If one wishes to get the result of the code, use the
     * [UPnPResult.wasSuccessful] method found inside of
     * [UPnPResult]. A callback for when the task finishes can also be set
     * using the [UPnPResult.onFinish] method.
     *
     * @param port
     * the port to forward.
     * @return the result of the execution.
     * @throws IllegalArgumentException
     * if the port is not within the range of `0-65535`.
     */
    @Synchronized
    @Throws(IllegalArgumentException::class)
    fun forwardPort(port: Int): UPnPResult {
        require(!(port < 0x0000 || port > 0xFFFF)) { "Port must be in between 0-65535" }
        val result: UPnPResult = object : UPnPResult() {
            override fun run() {
                name = "jraknet-port-forwarder-$port"
                success = UPnP.openPortUDP(port)
                finished = true
                if (runnable != null) {
                    runnable!!.run()
                }
            }
        }
        result.start()
        return result
    }

    /**
     * Closes the specified UDP port via
     * [UPnP](https://en.wikipedia.org/wiki/Universal_Plug_and_Play).
     *
     *
     * In order for this method to work,
     * [UPnP](https://en.wikipedia.org/wiki/Universal_Plug_and_Play)
     * for the router must be enabled. The way to enable this varies depending
     * on the router. There is no guarantee this method will successfully close
     * the specified UDP port; as it is completely dependent on the gateway (the
     * router in this case) to do so.
     *
     *
     * This is not a blocking method. However the code required to accomplish
     * the task can up to three seconds to execute. As a result, it is
     * encapsulated within another thread so as to prevent unnecessary blocking.
     * If one wishes to get the result of the code, use the
     * [UPnPResult.wasSuccessful] method found inside of
     * [UPnPResult]. A callback for when the task finishes can also be set
     * using the [UPnPResult.onFinish] method.
     *
     * @param port
     * the port to close.
     * @return the result of the execution.
     * @throws IllegalArgumentException
     * if the port is not within the range of `0-65535`.
     */
    @Synchronized
    @Throws(IllegalArgumentException::class)
    fun closePort(port: Int): UPnPResult {
        require(!(port < 0x0000 || port > 0xFFFF)) { "Port must be in between 0-65535" }
        val result: UPnPResult = object : UPnPResult() {
            override fun run() {
                name = "jraknet-port-closer-$port"
                success = UPnP.closePortUDP(port)
                finished = true
                if (runnable != null) {
                    runnable!!.run()
                }
            }
        }
        result.start()
        return result
    }

    /**
     * Sends a packet to the specified address.
     *
     * @param address
     * the address to send the packet to.
     * @param packet
     * the packet to send.
     * @param timeout
     * how long to wait until resending the packet.
     * @param retries
     * how many times the packet will be sent before giving up.
     * @return the packet received in response, `null` if no response
     * was received or the thread was interrupted.
     * @throws NullPointerException
     * if the `address`, IP address of the
     * `address`, or `packet` are
     * `null`.
     * @throws IllegalArgumentException
     * if the `timeout` or `retries` are less
     * than or equal to `0`.
     */
    @Throws(NullPointerException::class, IllegalArgumentException::class)
    private fun createBootstrapAndSend(
        address: InetSocketAddress?, packet: Packet?, timeout: Long,
        retries: Int
    ): RakNetPacket? {
        var retries = retries
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        } else if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        } else require(timeout > 0) { "Timeout must be greater than 0" }
            require(retries > 0) { "Retriest must be greater than 0" }

        // Prepare bootstrap
        var received: RakNetPacket? = null
        val group: EventLoopGroup = NioEventLoopGroup()
        val maximumTransferUnit = maximumTransferUnit
        if (maximumTransferUnit < MINIMUM_MTU_SIZE) {
            return null
        }
        try {
            // Create bootstrap
            val bootstrap = Bootstrap()
            val handler = BootstrapHandler()
            bootstrap.group(group).channel(NioDatagramChannel::class.java).option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, maximumTransferUnit)
                .option(ChannelOption.SO_SNDBUF, maximumTransferUnit).handler(handler)
            val channel = bootstrap.bind(0).sync().channel()

            // Wait for response
            while (retries > 0 && received == null && !Thread.currentThread().isInterrupted) {
                val sendTime = System.currentTimeMillis()
                channel.writeAndFlush(DatagramPacket(packet.buffer(), address))
                while (System.currentTimeMillis() - sendTime < timeout && handler.packet == null);
                received = handler.packet
                retries--
            }
        } catch (e: InterruptedException) {
            return null
        }
        group.shutdownGracefully()
        return received
    }

    /**
     * Returns whether or not the server with the specified address is online.
     *
     * @param address
     * the address of the server.
     * @return `true` if the server is online, `false`
     * otherwise.
     * @throws NullPointerException
     * if the `address` or the IP address of the
     * `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun isServerOnline(address: InetSocketAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        val connectionRequestOne = OpenConnectionRequestOne()
        connectionRequestOne.maximumTransferUnit = MINIMUM_MTU_SIZE
        connectionRequestOne.networkProtocol = CLIENT_NETWORK_PROTOCOL
        connectionRequestOne.encode()
        val packet = createBootstrapAndSend(address, connectionRequestOne, 1000, PING_RETRIES)
        if (packet != null) {
            if (packet.id.toInt() == PacketConstants.SERVER_TO_CLIENT_HANDSHAKE_1) {
                val connectionResponseOne = OpenConnectionResponseOne(packet)
                connectionResponseOne.decode()
                if (connectionResponseOne.magic == true) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Returns whether or not the server with the specified address is online.
     *
     * @param address
     * the IP address of the server.
     * @param port
     * the port of the server.
     * @return `true` if the server is online, `false`
     * otherwise.
     * @throws NullPointerException
     * if the `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun isServerOnline(address: InetAddress?, port: Int): Boolean {
        if (address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isServerOnline(InetSocketAddress(address, port))
    }

    /**
     * Returns whether or not the server with the specified address is online.
     *
     * @param host
     * the IP address of the server.
     * @param port
     * the port of the server.
     * @return `true` if the server is online, `false`
     * otherwise.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    @Throws(NullPointerException::class, UnknownHostException::class)
    fun isServerOnline(host: String?, port: Int): Boolean {
        if (host == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isServerOnline(InetAddress.getByName(host), port)
    }

    /**
     * Returns whether or not the server with the specified address is
     * compatible with the current client protocol.
     *
     * @param address
     * the address of the server.
     * @return `true` if the server is compatible with the current
     * client protocol, `false` otherwise.
     * @throws NullPointerException
     * if the `address` or the IP address of the
     * `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun isServerCompatible(address: InetSocketAddress?): Boolean {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        val connectionRequestOne = OpenConnectionRequestOne()
        connectionRequestOne.maximumTransferUnit = MINIMUM_MTU_SIZE
        connectionRequestOne.networkProtocol = CLIENT_NETWORK_PROTOCOL
        connectionRequestOne.encode()
        val packet = createBootstrapAndSend(address, connectionRequestOne, 1000L, PING_RETRIES)
        if (packet != null) {
            if (packet.id.toInt() == PacketConstants.SERVER_TO_CLIENT_HANDSHAKE_1) {
                val connectionResponseOne = OpenConnectionResponseOne(packet)
                connectionResponseOne.decode()
                if (connectionResponseOne.magic) {
                    return true
                }
            } else if (packet.id.toInt() == PacketConstants.INCOMPATIBLE_PROTOCOL) {
                val incompatibleProtocol = IncompatibleProtocolVersion(packet)
                incompatibleProtocol.decode()
                return false
            }
        }
        return false
    }

    /**
     * Returns whether or not the server with the specified address is
     * compatible with the current client protocol.
     *
     * @param address
     * the IP address of the server.
     * @param port
     * the port of the server.
     * @return `true` if the server is compatible with the current
     * client protocol, `false` otherwise.
     * @throws NullPointerException
     * if the `address` is `null`.
     */
    @Throws(NullPointerException::class)
    fun isServerCompatible(address: InetAddress?, port: Int): Boolean {
        if (address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isServerCompatible(InetSocketAddress(address, port))
    }

    /**
     * Returns whether or not the server with the specified address is
     * compatible with the current client protocol.
     *
     * @param host
     * the IP address of the server.
     * @param port
     * the port of the server.
     * @return `true` if the server is compatible with the current
     * client protocol, `false` otherwise.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, or
     * if a `scope_id` was specified for a global IPv6
     * address.
     */
    @Throws(NullPointerException::class, UnknownHostException::class)
    fun isServerCompatible(host: String?, port: Int): Boolean {
        if (host == null) {
            throw NullPointerException("IP address cannot be null")
        }
        return isServerCompatible(InetAddress.getByName(host), port)
    }



    /**
     * Returns the maximum transfer unit of the network card with the specified
     * address.
     *
     * @param address
     * the IP address. A `null` value will have the lowest
     * valid maximum transfer unit be returned instead.
     * @return the maximum transfer unit of the network card with the specified
     * address, `-1` if it could not be determined.
     * @throws RuntimeException
     * if an exception is caught when determining the lowest valid
     * maximum transfer unit size despite the safe checks put in
     * place.
     */


    @JvmName("getMaximumTransferUnit1")
    fun getMaximumTransferUnit(): Int {
        // Calculate lowest valid maximum transfer unit
        if (_lowestMaximumTransferUnitSize < 0) {
            try {
                var lowestMaximumTransferUnitSize = Int.MAX_VALUE
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    if (lowestMaximumTransferUnitSize > networkInterface.mtu && networkInterface.mtu >= 0) {
                        lowestMaximumTransferUnitSize = networkInterface.mtu
                    }
                }
                _lowestMaximumTransferUnitSize = lowestMaximumTransferUnitSize
            } catch (e: SocketException) {
                throw RuntimeException(e)
            } catch (e: NullPointerException) {
                throw RuntimeException(e)
            }
        }


            return _lowestMaximumTransferUnitSize

    }

    /**
     * Returns the maximum transfer unit of the network card with the specified
     * address.
     *
     * @param address
     * the IP address. A `null` value will have the lowest
     * valid maximum transfer unit be returned instead.
     * @return the maximum transfer unit of the network card with the specified
     * address, `-1` if it could not be determined.
     * @throws RuntimeException
     * if an exception is caught when determining the lowest valid
     * maximum transfer unit size despite the safe checks put in
     * place.
     */

    @Throws(RuntimeException::class)
    fun getMaximumTransferUnit(address: InetAddress?): Int {
        // Calculate lowest valid maximum transfer unit
        if (_lowestMaximumTransferUnitSize < 0) {
            try {
                var lowestMaximumTransferUnitSize = Int.MAX_VALUE
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    if (lowestMaximumTransferUnitSize > networkInterface.mtu && networkInterface.mtu >= 0) {
                        lowestMaximumTransferUnitSize = networkInterface.mtu
                    }
                }
                _lowestMaximumTransferUnitSize = lowestMaximumTransferUnitSize
            } catch (e: SocketException) {
                throw RuntimeException(e)
            } catch (e: NullPointerException) {
                throw RuntimeException(e)
            }
        }

        // Get maximum transfer unit for address
        if (address == null) {
            return _lowestMaximumTransferUnitSize
        } else if (!MAXIMUM_TRANSFER_UNIT_SIZES.containsKey(address)) {
            try {
                val maximumTransferUnit = NetworkInterface.getByInetAddress(address).mtu
                if (maximumTransferUnit < 0) {
                    throw SocketException("Invalid maximum transfer unit with size $maximumTransferUnit")
                }
                MAXIMUM_TRANSFER_UNIT_SIZES[address] = maximumTransferUnit
            } catch (e: SocketException) {
                MAXIMUM_TRANSFER_UNIT_SIZES[address] = _lowestMaximumTransferUnitSize
            } catch (e: NullPointerException) {
                MAXIMUM_TRANSFER_UNIT_SIZES[address] = _lowestMaximumTransferUnitSize
            }
        }
        return MAXIMUM_TRANSFER_UNIT_SIZES[address]!!.toInt()
    }

    /**
     * Returns the maximum transfer unit of the network card with the specified
     * address.
     *
     *
     * This method is simply a shorthand for
     * [.getMaximumTransferUnit], with the port of the
     * `address` not being used. Instead, the
     * [InetSocketAddress.getAddress] method is called to retrieve the
     * original [InetAddress]. If the `address` is
     * `null`, no `NullPointerException` will be thrown as
     * the possibility of a `null` value is accounted for.
     *
     * @param address
     * the address. A `null` value will have the lowest
     * valid maximum transfer unit be returned instead.
     * @return the maximum transfer unit of the network card with the specified
     * address, `-1` if it could not be determined.
     * @throws RuntimeException
     * if an exception is caught when determining the lowest valid
     * maximum transfer unit size despite the safe checks put in
     * place.
     */
    fun getMaximumTransferUnit(address: InetSocketAddress?): Int {
        return getMaximumTransferUnit(address?.address)
    }/* Solves ambiguity */

    /**
     * Returns the lowest valid maximum transfer unit among all of the network
     * cards installed on the machine.
     *
     * @return the lowest valid maximum transfer unit among all of the network
     * cards installed on the machine.
     */
    val maximumTransferUnit: Int
        get() = getMaximumTransferUnit( /* Solves ambiguity */null as InetAddress?)
    /**
     * Returns how many packets can be received in the span of a single second
     * before an address is automatically blocked.
     *
     * @return how many packets can be received in the span of a single second
     * before an address is automatically blocked.
     *///		LOGGER.info("Set max packets per second to " + maxPacketsPerSecond);
    /**
     * Sets how many packets can be received in the span of a single second
     * before an address is blocked.
     *
     *
     * One must take caution when setting this value, as setting it to low can
     * cause communication to become impossible.
     *
     * @param maxPacketsPerSecond
     * how many packets can be received in the span of a single
     * second before a peer is blocked.
     * @throws IllegalArgumentException
     * if `maxPacketsPerSecond` is negative.
     */
    @JvmStatic
	@set:Throws(IllegalArgumentException::class)
    var maxPacketsPerSecond: Long
        get() = _maxPacketsPerSecond
        set(maxPacketsPerSecond) {
            require(maxPacketsPerSecond >= 0) { "Max packets per second cannot be negative" }
            val updated = _maxPacketsPerSecond != maxPacketsPerSecond
            _maxPacketsPerSecond = maxPacketsPerSecond
        }
    /**
     * Returns the amount of system addresses sent in the
     * [ CONNECTION_REQUEST_ACCEPTED][bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted] packet.
     *
     * @return the amount of system addresses sent in the
     * [         CONNECTION_REQUEST_ACCEPTED][bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted] packet.
     *///		LOGGER.info("Set system address count to " + systemAddressCount);
    /**
     * Sets the amount of system addresses to send in the
     * [ CONNECTION_REQUEST_ACCEPTED][bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted] packet.
     *
     *
     * Normally this does not need to be changed. However, some games have a
     * custom amount of system addresses that are sent during login.
     *
     * @param systemAddressCount
     * the system address count.
     * @throws IllegalArgumentException
     * if the `systemAddressCount` is less than
     * {@value #RAKNET_SYSTEM_ADDRESS_COUNT}.
     */
    @JvmStatic
	@set:Throws(IllegalArgumentException::class)
    var systemAddressCount: Int
        get() = _systemAddressCount
        set(systemAddressCount) {
            require(systemAddressCount >= RAKNET_SYSTEM_ADDRESS_COUNT) { "System address count cannot be less than $RAKNET_SYSTEM_ADDRESS_COUNT" }
            val updated = _systemAddressCount != systemAddressCount
            _systemAddressCount = systemAddressCount
        }

    /**
     * Converts the specified ID to a hex string.
     *
     * @param id
     * the ID to convert to a hex string.
     * @return the generated hex string.
     */
    fun toHexStringId(id: Int): String {
        var hexString = Integer.toHexString(id)
        if (hexString.length % 2 != 0) {
            hexString = "0$hexString"
        }
        return "0x" + hexString.uppercase()
    }

    /**
     * Converts the ID of the specified packet to a hex string.
     *
     * @param packet
     * the packet to get the ID from.
     * @return the generated hex string.
     * @throws NullPointerException
     * if the `packet` is `null`.
     */
    @Throws(NullPointerException::class)
    fun toHexStringId(packet: RakNetPacket?): String {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        return toHexStringId(packet.id.toInt())
    }

    /**
     * Parses the specified string to a `long`.
     *
     * @param longStr
     * the string to parse.
     * @return the string as a `long`, `-1` if it fails to
     * parse.
     */
	@JvmStatic
	fun parseLongPassive(longStr: String): Long {
        return try {
            longStr.toLong()
        } catch (e: NumberFormatException) {
            -1L // Failed to parse
        }
    }

    /**
     * Parses the specified string to an `int`.
     *
     * @param intStr
     * the string to parse.
     * @return the string as an `int`, `-1` if it fails to
     * parse.
     */
	@JvmStatic
	fun parseIntPassive(intStr: String): Int {
        return parseLongPassive(intStr).toInt()
    }

    /**
     * Parses the specified string to a `short`.
     *
     * @param shortStr
     * the string to parse.
     * @return the string as a `short`, `-1` if fails to
     * parse.
     */
    fun parseShortPassive(shortStr: String): Short {
        return parseLongPassive(shortStr).toShort()
    }

    /**
     * Parses the specified string to a `byte`.
     *
     * @param byteStr
     * the string to parse.
     * @return the string as a `byte`, `-1` if it fails to
     * parse.
     */
    fun parseBytePassive(byteStr: String): Byte {
        return parseLongPassive(byteStr).toByte()
    }

    /**
     * Parses a single String as an address and port and converts it to an
     * `InetSocketAddress`.
     *
     * @param address
     * the address to convert.
     * @param defaultPort
     * the default port to use if one is not.
     * @return the parsed `InetSocketAddress`.
     * @throws UnknownHostException
     * if the address is in an invalid format or if the host cannot
     * be found.
     */
    @Throws(UnknownHostException::class)
    fun parseAddress(address: String, defaultPort: Int): InetSocketAddress {
        val addressSplit = address.split(":").toTypedArray()
        return if (addressSplit.size == 1 || addressSplit.size == 2) {
            val inetAddress = InetAddress.getByName(
                if (!addressSplit[0].startsWith("/")) addressSplit[0] else addressSplit[0].substring(
                    1,
                    addressSplit[0].length
                )
            )
            val port = if (addressSplit.size == 2) parseIntPassive(addressSplit[1]) else defaultPort
            if (port in 0x0000..0xFFFF) {
                InetSocketAddress(inetAddress, port)
            } else {
                throw UnknownHostException("Port number must be between 0-65535")
            }
        } else {
            throw UnknownHostException("Format must follow address:port")
        }
    }

    /**
     * Parses a single String as an address and port and converts it to an
     * `InetSocketAddress`.
     *
     * @param address
     * the address to convert.
     * @return the parsed `InetSocketAddress`.
     * @throws UnknownHostException
     * if the address is in an invalid format, the host cannot be
     * found, or no port was specified in the `address`.
     */
    @Throws(UnknownHostException::class)
    fun parseAddress(address: String): InetSocketAddress {
        return try {
            parseAddress(address, -1 /* If no port specified */)
        } catch (e: IllegalArgumentException) {
            throw UnknownHostException("No port specified in address")
        }
    }
    /**
     * Parses a single String as an address and port and converts it to an
     * `InetSocketAddress`.
     *
     * @param address
     * the address to convert.
     * @return the parsed `InetSocketAddress`, `null` if
     * it fails to parse.
     */
    @JvmOverloads
    fun parseAddressPassive(address: String, defaultPort: Int = -1): InetSocketAddress? {
        return try {
            parseAddress(address, defaultPort)
        } catch (e: UnknownHostException) {
            null // Unknown host
        }
    }

    /**
     * A `Thread` which runs in the background to allow for code
     * relating to UPnP to be executed through the WaifUPnP without locking up
     * the main thread.
     *
     * @author "Whirvis" Trent Summerlin
     * @since JRakNet v2.11.0
     * @see .onFinish
     * @see .wasSuccessful
     */
    open class UPnPResult : Thread() {
        protected var finished = false
        protected var runnable: Runnable? = null
        protected var success = false

        /**
         * Sets the callback for when the task has finished executing.
         *
         *
         * This callback method will be run on the same thread as the original
         * task.
         *
         * @param runnable
         * the callback.
         */
        fun onFinish(runnable: Runnable?) {
            this.runnable = runnable
        }

        /**
         * Returns whether or not the UPnP task was successful.
         *
         * @return `true` if the UPnP task was successful,
         * `false` otherwise.
         * @throws IllegalStateException
         * if the UPnP code is still being executed.
         */
        @Throws(IllegalStateException::class)
        fun wasSuccessful(): Boolean {
            check(finished) { "UPnP code is still being executed" }
            return success
        }
    }

    /**
     * Used by the
     * [RakNet.createBootstrapAndSend]
     * method to wait for a packet.
     *
     * @author "Whirvis" Trent Summerlin
     * @since JRakNet v1.0.0
     */
    private class BootstrapHandler : ChannelInboundHandlerAdapter() {
        @Volatile
        var packet: RakNetPacket? = null
        @Throws(Exception::class)
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            if (msg is DatagramPacket) {
                packet = RakNetPacket(msg.content())
            }
        }

        override fun channelReadComplete(ctx: ChannelHandlerContext) {
            ctx.flush()
        }
    }
}