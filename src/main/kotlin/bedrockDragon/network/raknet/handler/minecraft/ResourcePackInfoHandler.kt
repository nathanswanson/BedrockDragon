package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.game.GamePacket
import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.ResourceInfoPacket

class ResourcePackInfoHandler(peer: RakNetPeer) : ReflectMinecraftHandler(peer) {
    init {

        val packet = ResourceInfoPacket()
        packet.forcedToAccept = true
        packet.scriptingEnabled = true
        packet.resourcePackInfos = emptyArray()
        packet.behaviourPackInfos = emptyArray()
        packet.encode()

        peer.sendMessage(Reliability.UNRELIABLE, 0, GamePacket.create(packet))
    }


}