package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.net.InetSocketAddress

class TransferPacket: PacketPayload(MinecraftPacketConstants.TRANSFER) {
    lateinit var address: InetSocketAddress

    override suspend fun encode() {
        writeAddress(address)
    }
}