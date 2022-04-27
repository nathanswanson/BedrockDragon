package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.resourcepacket.ResourceStackPacket
import kotlinx.coroutines.runBlocking

class ResourcePackStackHandler(peer: RakNetPeer) : ReflectMinecraftHandler(peer) {
    init {
        val packet = ResourceStackPacket()
        packet.forcedToAccept = false
        packet.behaviorPackEntry = emptyArray()
        packet.resourcePackEntry = emptyArray()
        packet.experimental = false
        packet.gameVersion = "1.18.40"
        runBlocking { packet.encode() }

        peer.sendMessage(Reliability.RELIABLE_ORDERED, 0, MinecraftPacket.encapsulateGamePacket(packet, MinecraftPacketConstants.RESOURCE_PACK_STACK, null))

    }
}