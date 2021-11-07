package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.handler.MinecraftHandler
import bedrockDragon.network.raknet.protocol.game.util.MalformPacket

class MalformHandler(packet: Packet): MinecraftHandler() {
    init {
        val malformPacket = MalformPacket()
        malformPacket.decode(packet)
    }
    override fun pass() {

    }
}