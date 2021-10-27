package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants

class ClientCacheHandler(peer: RakNetPeer, packet: MinecraftPacket): ReflectMinecraftHandler(peer) {
    init {
        //this packet is simple, one boolean so I'll just rip header and boolean without object

       // val clientCacheEnabled = packet.pay[1].toInt() == 1

        //if(clientCacheEnabled) {
        //    (peer as RakNetClientPeer).cacheEnabled = true
       //     packet.encode(true)
       ///     peer.sendMessage(Reliability.UNRELIABLE, 0, packet)
        //}
    }
}