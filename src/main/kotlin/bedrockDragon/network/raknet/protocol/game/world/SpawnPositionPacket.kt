package bedrockDragon.network.raknet.protocol.game.world

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class SpawnPositionPacket: PacketPayload(MinecraftPacketConstants.SET_SPAWN_POSITION) {
    var spawnType = -1 //sVarInt
    lateinit var blockPosition: Float3
    var dimensionId = 0
    lateinit var spawnPosition: Float3
    var forced = false;

    override fun encode() {
        writeVarInt(spawnType)
        writeBlockCoordinates(blockPosition)
        writeInt(dimensionId)
        writeVector3(spawnPosition)
        writeBoolean(forced)
    }
}