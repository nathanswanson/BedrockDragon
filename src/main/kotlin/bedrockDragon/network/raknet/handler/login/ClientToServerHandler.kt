package bedrockDragon.network.raknet.handler.login

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.handler.MinecraftHandler
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.connect.ClientToServerHandshakePacket

class ClientToServerHandler(packet: Packet): MinecraftHandler() {
    init {
        val pk = ClientToServerHandshakePacket()
        pk.decode(packet)
    }

    override fun pass() {
    }

}