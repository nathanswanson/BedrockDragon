package bedrockDragon.network.raknet.util

import io.netty.bootstrap.AbstractBootstrap
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.epoll.Native
import io.netty.channel.unix.UnixChannelOption
import io.netty.util.concurrent.Future
import java.util.*
import java.util.concurrent.CompletableFuture


object Bootstraps {
    val kernelVersion: Optional<IntArray>? = null
    private val REUSEPORT_VERSION = intArrayOf(3, 9, 0)
    const val isReusePortAvailable = false

    fun setupBootstrap(bootstrap: Bootstrap, datagram: Boolean) {
        //val channel: Class<out Channel> = if (datagram) CHANNEL_TYPE.datagramChannel else CHANNEL_TYPE.socketChannel
        //bootstrap.channel(channel)
        //setupAbstractBootstrap(bootstrap)
    }

    fun setupServerBootstrap(bootstrap: ServerBootstrap) {
        //val channel: Class<out ServerSocketChannel?> = CHANNEL_TYPE.serverSocketChannel
        //bootstrap.channel(channel)
        //setupAbstractBootstrap(bootstrap)
    }

    private fun setupAbstractBootstrap(bootstrap: AbstractBootstrap<*, *>) {
        if (isReusePortAvailable) {
            bootstrap.option(UnixChannelOption.SO_REUSEPORT, true)
        }
    }

    private fun fromString(ver: String): IntArray {
        val parts = ver.split("\\.").toTypedArray()
        require(parts.size >= 2) { "At least 2 version numbers required" }
        return intArrayOf(
            parts[0].toInt(), parts[1].toInt(),
            if (parts.size == 2) 0 else parts[2].toInt()
        )
    }

    private fun checkVersion(ver: IntArray, i: Int): Boolean {
        if (ver[i] > REUSEPORT_VERSION[i]) {
            return true
        } else if (ver[i] == REUSEPORT_VERSION[i]) {
            return if (ver.size == i + 1) {
                true
            } else {
                checkVersion(ver, i + 1)
            }
        }
        return false
    }

    fun allOf(vararg futures: ChannelFuture): CompletableFuture<Void?> {
        if (futures == null || futures.size == 0) {
            return CompletableFuture.completedFuture(null)
        }
        val completableFutures: Array<CompletableFuture<*>?> = arrayOfNulls(futures.size)
        for (i in futures.indices) {
            val channelFuture = futures[i]
            val completableFuture = CompletableFuture<Channel>()
            channelFuture.addListener { future: Future<in Void?> ->
                if (future.cause() != null) {
                    completableFuture.completeExceptionally(future.cause())
                }
                completableFuture.complete(channelFuture.channel())
            }
            completableFutures[i] = completableFuture
        }
        return CompletableFuture.allOf(*completableFutures)
    }

    init {
        var kernelVersion: String?
        kernelVersion = try {
            Native.KERNEL_VERSION
        } catch (e: Throwable) {
            null
        }
        if (kernelVersion != null && kernelVersion.contains("-")) {
            val index = kernelVersion.indexOf('-')
            if (index > -1) {
                kernelVersion = kernelVersion.substring(0, index)
            }
            val kernelVer = fromString(kernelVersion)
            //Bootstraps.kernelVersion = Optional.of(kernelVer)
            //isReusePortAvailable = checkVersion(kernelVer, 0)
        } else {
            //Bootstraps.kernelVersion = Optional.empty()
            //isReusePortAvailable = false
        }
    }
}