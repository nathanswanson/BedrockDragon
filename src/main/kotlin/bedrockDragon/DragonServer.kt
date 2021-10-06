package bedrockDragon

import bedrockDragon.network.protocol.PacketSortFactory
import bedrockDragon.network.protocol.packethandler.ConnectionRequestHandlerTwo
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.ThreadedListener
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.Status
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import kotlin.collections.HashMap

private val logger = KotlinLogging.logger {}

class DragonServer(private val bindAddress: InetSocketAddress): RakNetServerListener {

    private var eventThreadCount = 0
    private var isRunning = false
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val startTimeStamp = System.currentTimeMillis()
    private var bootstrap: Bootstrap = Bootstrap()
    private var group: NioEventLoopGroup = NioEventLoopGroup()
    var clients: HashMap<InetSocketAddress, RakNetClientPeer> = HashMap()
    private lateinit var channel: Channel
    private lateinit var handle : RakNetServerHandler
    private lateinit var listeners: ConcurrentLinkedQueue<RakNetServerListener>

    companion object {
        var guid : Long = 0
        var pongId : Long = 0
        var mtu = RakNet.getMaximumTransferUnit()
    }

    fun start() {

        val uuid = UUID.randomUUID()

        guid = uuid.mostSignificantBits
        pongId = uuid.leastSignificantBits

        mtu = RakNet.getMaximumTransferUnit(bindAddress)
        logger.info { "Starting RakNet" }
        handle = RakNetServerHandler(this)

        bootstrap.handler(handle)
        bootstrap.channel(NioDatagramChannel::class.java).group(group)
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

    }

    private fun tick() {
        while(isRunning) {
            for (peer in clients.values) {
                if (peer.status == Status.CONNECTED) {
                    peer.update()
                }
            }
        }
    }

    fun stop() {
        isRunning = false
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

    @Throws(NullPointerException::class)
    fun callEvent(event: Consumer<in RakNetServerListener?>?) {
        if (event == null) {
            throw NullPointerException("Event cannot be null")
        }
        //logger.trace("Called event of class " + event.getClass().getName() + " for " + listeners.size() + " listeners");
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


    }

    fun handleHandlerException(causeAddress: InetSocketAddress, cause: Throwable) {

    }

    fun timeStamp(): Long {
        return System.currentTimeMillis() - startTimeStamp
    }


}