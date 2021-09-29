package bedrockDragon

import com.nukkitx.protocol.bedrock.BedrockServer
import java.net.InetSocketAddress

class Server(bindAddress: InetSocketAddress?) : BedrockServer(bindAddress) {

    private var isRunning = false;

    public fun start() {


        //Start server tick.
        isRunning = true
        tick()
    }

    private fun tick() {
        while(isRunning) {

        }
    }

    public fun stop() {
        isRunning = false
    }
}