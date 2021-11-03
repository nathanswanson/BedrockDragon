package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.handler.MinecraftHandler
import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.MinecraftPeer
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.connect.PlayStatusPacket

class PlayStatusHandler(val status: Int, peer: RakNetClientPeer): ReflectMinecraftHandler(peer) {
    init {
        val response = PlayStatusPacket(status)
        peer.sendMessage(Reliability.UNRELIABLE, 0 , response)
    }
}