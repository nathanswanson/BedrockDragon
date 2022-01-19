package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.net.InetSocketAddress

class AutomationClientConnectPacket: PacketPayload(MinecraftPacketConstants.AUTOMATION_CLIENT_CONNECT) {
    lateinit var address: InetSocketAddress

    override fun encode() {
        writeAddress(address)
    }
}