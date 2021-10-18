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

import bedrockDragon.network.raknet.handler.PacketSortFactory
import bedrockDragon.network.raknet.handler.packethandler.login.ConnectionRequestHandlerTwo
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.ThreadedListener
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.server.RakNetServerHandler
import bedrockDragon.network.raknet.server.RakNetServerListener
import bedrockDragon.ticking.ChunkTicker
import bedrockDragon.ticking.EntityTicker
import bedrockDragon.ticking.WorldTicker
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
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
class DragonServer(private val bindAddress: InetSocketAddress): RakNetServerListener {

    private var eventThreadCount = 0
    private val logger = KotlinLogging.logger {}
    private var isRunning = false
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val startTimeStamp = System.currentTimeMillis()
    private var bootstrap: Bootstrap = Bootstrap()
    private var group: NioEventLoopGroup = NioEventLoopGroup()
    var clients: ConcurrentHashMap<InetSocketAddress, RakNetClientPeer> = ConcurrentHashMap()
    private lateinit var channel: Channel
    private lateinit var handle : RakNetServerHandler
    private lateinit var listeners: ConcurrentLinkedQueue<RakNetServerListener>
    companion object {
        var guid : Long = 0
        var pongId : Long = 0
        var mtu = RakNet.getMaximumTransferUnit()
    }

    fun start(): Boolean {

        val uuid = UUID.randomUUID()

        guid = uuid.mostSignificantBits
        pongId = uuid.leastSignificantBits
        mtu = RakNet.getMaximumTransferUnit(bindAddress)
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

        //Coroutine Entity
        logger.info { "Starting Entity Thread" }
        scope.launch { entityLightThread() }
        //Coroutine World
        logger.info { "Starting World Thread" }
        scope.launch { worldLightThread() }
        //Coroutine Chunk
        logger.info { "Starting Chunk Thread" }
        scope.launch { chunkLightThread() }
        //Start server tick.
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

    fun stop(): Boolean {
        isRunning = false
        return true
    }

    private fun entityLightThread() {
        val entityTicker = EntityTicker()
        entityTicker.initialize()
    }

    private fun worldLightThread() {
        val worldTicker = WorldTicker()
        worldTicker.initialize()
    }

    private fun chunkLightThread() {
        val chunkTicker = ChunkTicker()
        chunkTicker.tick()
    }

    fun disconnect(client: RakNetClientPeer, s: String) {

    }

    //TODO COMPLETE REDO

    @Throws(NullPointerException::class)
    fun callEvent(event: MessagePassingQueue.Consumer<in RakNetServerListener?>?) {
        if (event == null) {
            throw NullPointerException("Event cannot be null")
        }
        for (listener: RakNetServerListener in listeners) {
            if (listener.javaClass.isAnnotationPresent(ThreadedListener::class.java)) {
                val threadedListener = listener.javaClass.getAnnotation(
                    ThreadedListener::class.java
                )
                object : Thread(
                    DragonServer::class.java.simpleName + (if (threadedListener.name.isNotEmpty()) "-" else "")
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
    }

    fun handleMessage(sender: InetSocketAddress, packet: RakNetPacket) {

        if(clients.containsKey(sender)) {
                clients[sender]!!.incomingPacket(packet)
        } else {
            val packetHandler = PacketSortFactory.createPacketHandle(sender, packet, channel)
            packetHandler.responseToClient()
            if(packetHandler is ConnectionRequestHandlerTwo && packetHandler.finished) {
                clients[sender] = RakNetClientPeer(this, packetHandler.connectionType, packetHandler.clientGuid, packetHandler.mtu, channel, sender)
            }
        }

        packet.buffer().release()
    }

    fun handleHandlerException(causeAddress: InetSocketAddress, cause: Throwable) {

    }

    fun timeStamp(): Long {
        return System.currentTimeMillis() - startTimeStamp
    }

}