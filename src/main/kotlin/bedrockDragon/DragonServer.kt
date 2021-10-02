package bedrockDragon

import bedrockDragon.network.bedrockprotocol.IPacketCoolDown
import bedrockDragon.network.bedrockprotocol.PacketSortFactory
import bedrockDragon.network.bedrockprotocol.packethandler.PacketHandler
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.ThreadedListener
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.server.RakNetServerHandler
import bedrockDragon.network.raknet.server.RakNetServerListener
import bedrockDragon.ticking.EntityTicker
import bedrockDragon.ticking.WorldTicker
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.FixedRecvByteBufAllocator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer
import kotlin.math.log

private val logger = KotlinLogging.logger {}

class DragonServer(private val bindAddress: InetSocketAddress): RakNetServerListener {

    private var eventThreadCount = 0
    private var isRunning = false
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var bootstrap: Bootstrap = Bootstrap()
    private var group: NioEventLoopGroup = NioEventLoopGroup()

    private lateinit var channel: Channel
    private lateinit var handle : RakNetServerHandler
    private lateinit var listeners: ConcurrentLinkedQueue<RakNetServerListener>

    fun start() {

        logger.info { "Starting RakNet" }
        handle = RakNetServerHandler(this)

        bootstrap.handler(handle)
        bootstrap.channel(NioDatagramChannel::class.java).group(group)
        bootstrap.option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_REUSEADDR, false)
            .option(ChannelOption.SO_SNDBUF, 256)
            .option(ChannelOption.SO_RCVBUF, 256)
            .option(ChannelOption.RCVBUF_ALLOCATOR, FixedRecvByteBufAllocator(256))

        channel = bootstrap.bind(bindAddress).sync().channel()



        //Coroutine Entity
        logger.info { "Starting Entity Thread" }
        scope.launch { entityLightThread() }
        //Coroutine World
        logger.info { "Starting World Thread" }
        scope.launch { worldLightThread() }
        //Start server tick.
        //Main thread deals with packets received and sent to client. packets received are converted into objects and sent to the related lightThread
        isRunning = true
        tick()

    }

    private fun tick() {
        //var lastTick = System.currentTimeMillis()
        while(isRunning) {
            //Run 20 ticks a second
            //1 second = 1000ms
            //1000ms/20 = 50
            //if(System.currentTimeMillis() - lastTick >= 50) {
            //    logger.info { "tick ${System.currentTimeMillis()}" }
           //     lastTick = System.currentTimeMillis()
           // }
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
        val packetHandler = PacketSortFactory.createPacketHandle(sender, packet, channel)


            packetHandler.responseToClient()

            logger.info { packetHandler }
            //packetHandler.responseToServer()






    }

    fun handleHandlerException(causeAddress: InetSocketAddress, cause: Throwable) {

    }

    fun clients(): ArrayList<RakNetClientPeer> {
        return TODO()
    }

}