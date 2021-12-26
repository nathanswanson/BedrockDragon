/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon

import bedrockDragon.DragonServer.ServerHandlerFactory.guid
import bedrockDragon.DragonServer.ServerHandlerFactory.pongId
import bedrockDragon.network.Peer
import bedrockDragon.network.raknet.handler.login.ConnectionRequestHandlerTwo
import bedrockDragon.network.raknet.RakNetPacket
//import bedrockDragon.network.raknet.ThreadedListener
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.handler.PacketHandler
import bedrockDragon.network.raknet.handler.connect.ConnectedPingHandler
import bedrockDragon.network.raknet.handler.connect.ConnectionRequestHandlerPost
import bedrockDragon.network.raknet.handler.connect.DisconnectHandler
import bedrockDragon.network.raknet.handler.connect.IncomingConnectionHandler
import bedrockDragon.network.raknet.handler.login.ConnectionRequestHandlerOne
import bedrockDragon.network.raknet.handler.login.LoginHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.server.RakNetServerHandler
import bedrockDragon.network.raknet.server.RakNetServerListener
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.ResourceLeakDetector
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The heart of the entire server. this object initializes all coroutines,
 * raknet thread, and client update thread(main)
 *
 * All packets from the internet are passed through here first. See
 * [handleMessage]
 *
 * All events are fired from this object. See [callEvent]
 *
 * @author Nathan Swanson
 * @since Bedrock Dragon ALPHA
 */
class DragonServer(private val bindAddress: InetSocketAddress): RakNetServerListener, Peer(bindAddress) {

    private var eventThreadCount = 0
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val startTimeStamp = System.currentTimeMillis()
    private var bootstrap: Bootstrap = Bootstrap()
    private var group: NioEventLoopGroup = NioEventLoopGroup()
    private var playerCount = 0
    //TODO MAKE PRIVATE
    var clients: ConcurrentHashMap<InetSocketAddress, RakNetClientPeer> = ConcurrentHashMap()

    //init in start
    private lateinit var channel: Channel
    private lateinit var handle : RakNetServerHandler
    private lateinit var listeners: ConcurrentLinkedQueue<RakNetServerListener>

    /**
     * Main method calls this after config and mods are loaded.
     * @author Nathan Swanson
     * @since ALPHA
     */
    override fun start(): Boolean {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID)
        val uuid = UUID.randomUUID()
        pongId = uuid.leastSignificantBits

        ServerHandlerFactory.guid = guid
        ServerHandlerFactory.mtu = mtu

        logger.info { "Starting RakNet" }

        handle = RakNetServerHandler(this)

        bootstrap.group(group)
        //bootstrap.handler(handle)

        //TODO add TCP and HTTP support
        bootstrap.channel(NioDatagramChannel::class.java)
        bootstrap.handler(RakNetServerHandler(this))
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, false)
            .option(ChannelOption.SO_SNDBUF, mtu)
            .option(ChannelOption.SO_RCVBUF, mtu)
            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(mtu + 500))

        channel = bootstrap.bind(bindAddress).sync().channel()


        //Main thread deals with packets received and sent to client. packets received are converted into objects and sent to the related lightThread
        isRunning = true
        tick()
        return true
    }

    private fun tick() {
        while(isRunning) {
            for (peer in clients.values) {
                    peer.update()
            }
        }
    }

    override fun stop(): Boolean {
        isRunning = false
        return true
    }



    fun disconnect(client: RakNetClientPeer, s: String) {
        try {
            client.destroy()
            clients.remove(client.sender)
            playerCount--
        } catch (e: java.lang.NullPointerException) {
            logger.info { "$client does not exist but disconnect was called! $clients" }
        }
    }
    //TODO COMPLETE REDO

    @Throws(NullPointerException::class)
    fun callEvent(event: MessagePassingQueue.Consumer<in RakNetServerListener?>?) {
        if (event == null) {
            throw NullPointerException("Event cannot be null")
        }
        for (listener: RakNetServerListener in listeners) {
                event.accept(listener)
            }
    }

    /**
     * Every packet sent from client to server starts here as of 11/03/2021
     * Do not call this function.
     * @author Nathan Swanson
     * @since ALPHA
     */
    fun handleMessage(sender: InetSocketAddress, packet: RakNetPacket) {
        if(clients.containsKey(sender)) {
                clients[sender]!!.incomingPacket(packet)
        } else {
            val packetHandler = ServerHandlerFactory.createPacketHandle(sender, packet, channel)
            packetHandler.responseToClient()
            if(packetHandler is ConnectionRequestHandlerTwo && packetHandler.finished) {
                clients[sender] = RakNetClientPeer(this, packetHandler.connectionType, packetHandler.clientGuid, packetHandler.clientmtu, channel, sender)
                //todo playercount incremented when minecraft login finalized not when raknet is.
                playerCount++
                logger.info { "Added Player" }
            }
        }

        packet.buffer().release()
    }

    fun handleHandlerException(causeAddress: InetSocketAddress, cause: Throwable) {

    }

    fun timeStamp(): Long {
        return System.currentTimeMillis() - startTimeStamp
    }

    object ServerHandlerFactory {
        private val logger = KotlinLogging.logger {}

        //set in server start()
        var pongId = 0L
        var guid = 0L
        var mtu = 0

        fun createPacketHandle(sender: InetSocketAddress, packet: RakNetPacket, channel: Channel) : PacketHandler {

            return when(packet.id.toInt()) {
                PacketConstants.UNCONNECTED_PING -> LoginHandler(sender, packet, channel, pongId)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_1 -> ConnectionRequestHandlerOne(sender, packet, channel, guid)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_2 -> ConnectionRequestHandlerTwo(sender, packet, channel, guid, mtu)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }

        }

        fun createEncapsulatedPacketHandle(sender: RakNetPeer, packet: EncapsulatedPacket, channel: Channel) : PacketHandler {

            return when(packet.payload.buffer().getUnsignedByte(0).toInt()) {
                PacketConstants.CONNECTED_PING -> ConnectedPingHandler(sender, packet, channel)
                PacketConstants.CONNECTION_REQUEST -> ConnectionRequestHandlerPost(sender, packet, channel)
                PacketConstants.NEW_INCOMING_CONNECTION -> IncomingConnectionHandler(sender as RakNetClientPeer, packet, channel)
                PacketConstants.CLIENT_DISCONNECT -> DisconnectHandler(sender as RakNetClientPeer, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }

        }
    }

    companion object {
        const val MINECRAFT_VERSION = "1.17.41"
    }
}