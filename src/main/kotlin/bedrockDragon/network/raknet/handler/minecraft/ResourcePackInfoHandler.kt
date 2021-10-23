package bedrockDragon.network.raknet.handler.minecraft

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.network.raknet.game.GamePacket
import bedrockDragon.network.raknet.handler.ReflectMinecraftHandler
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.ResourceInfoPacket
import bedrockDragon.network.raknet.protocol.game.type.ResourcePack
import java.util.*

class ResourcePackInfoHandler(peer: RakNetPeer) : ReflectMinecraftHandler(peer) {
    init {

        val packet = ResourceInfoPacket()

        packet.forcedToAccept = true
        packet.scriptingEnabled = true
        packet.resourcePackInfos = arrayOf(testResourcePack())
        packet.behaviourPackInfos = arrayOf(testResourcePack())
        packet.encode()

        val gameCapsule = GamePacket()
        gameCapsule.gamePacketContent = packet.array()!!
        gameCapsule.encode()
        peer.sendMessage(Reliability.RELIABLE_ORDERED, 0, gameCapsule)
    }

    class testResourcePack(
        override val packId: UUID = UUID.randomUUID(),
        override val packName: String = "test",
        override val packSize: Int = 4,
        override val packVersion: String = "1.0.0",
        override val sha256: ByteArray = ByteArray(0)
    ) : ResourcePack {
        override fun packChunk(start: Int, end: Int) {
            TODO("Not yet implemented")
        }

    }
}