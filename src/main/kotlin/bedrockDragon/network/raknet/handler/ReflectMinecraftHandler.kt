package bedrockDragon.network.raknet.handler

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.peer.MinecraftPeer
import bedrockDragon.network.raknet.peer.RakNetPeer

abstract class ReflectMinecraftHandler(peer: RakNetPeer): MinecraftHandler() {
    override fun pass() { /*Nothing it reflects Client -> Netty -> Client */ }
}