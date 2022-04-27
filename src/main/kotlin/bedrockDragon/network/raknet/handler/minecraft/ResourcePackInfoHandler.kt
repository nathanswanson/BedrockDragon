package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.resourcepacket.ResourceInfoPacket
import kotlinx.coroutines.runBlocking
import java.util.*

class ResourcePackInfoHandler(peer: RakNetPeer) : ReflectMinecraftHandler(peer) {
    init {

        val packet = ResourceInfoPacket()

        packet.forcedToAccept = false
        packet.scriptingEnabled = false
        packet.resourcePackInfos = emptyArray()
        packet.behaviourPackInfos = emptyArray()
        runBlocking { packet.encode() }



        peer.sendMessage(Reliability.RELIABLE_ORDERED, 0, MinecraftPacket.encapsulateGamePacket(packet, MinecraftPacketConstants.RESOURCE_PACKS_INFO, null))
    }
}