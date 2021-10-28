package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.resourcepacket.ResourceResponsePacket

class ResourcePackResponseHandler(packet: MinecraftPacket, peer: RakNetPeer): ReflectMinecraftHandler(peer) {
    init {
        (peer as RakNetClientPeer).attemptMinecraftHandoff()
    }
}