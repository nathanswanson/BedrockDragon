package bedrockDragon.debug.clientSimulator

import bedrockDragon.network.raknet.protocol.packet.packethandler.logger
import bedrockDragon.network.raknet.protocol.ConnectionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.net.InetSocketAddress


fun main() {
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    val client = RakNetClient()
    client.addListener(object : RakNetClientListener {
        // Connected to server
        override fun onConnect(client: RakNetClient?, address: InetSocketAddress?, connectionType: ConnectionType?) {
            println("Successfully connected to server with address $address")
        }

        // Logged into server
        override fun onLogin(client: RakNetClient?, peer: RakNetServerPeer?) {
            println("Successfully logged into server")
            client!!.disconnect()
        }

        // Disconnected from server
        override fun onDisconnect(
            client: RakNetClient?,
            address: InetSocketAddress?,
            peer: RakNetServerPeer?,
            reason: String?
        ) {
            println("Successfully disconnected from server with address $address for reason \"$reason\"")
        }
    })

    //scope.launch { bedrockDragon.main() }

    client.connect(InetSocketAddress("play.lbsg.net", 19132))
    logger.info { "client Connect" }
}