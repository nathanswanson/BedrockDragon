package bedrockDragon

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.net.InetSocketAddress

internal class DragonServerTest {

    @Test
    fun getClients() {

    }

    @Test
    fun setClients() {

    }

    @Test
    fun start() {
        val server = DragonServer(InetSocketAddress("0.0.0.0",19132))
        assert(server.start())
    }

    @Test
    fun stop() {
        val server = DragonServer(InetSocketAddress("0.0.0.0",19132))
        server.start()
        assert(server.stop())

    }

    @Test
    fun disconnect() {

    }

    @Test
    fun callEvent() {

    }

    @Test
    fun handleMessage() {

    }

    @Test
    fun handleHandlerException() {

    }

}