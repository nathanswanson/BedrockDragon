package bedrockDragon.network.raknet.protocol.game.block

import dev.romainguy.kotlin.math.Float3

class BlockEntityDataPacket {
    lateinit var coord: Float3 //blockCoord
    var namedTag = 0
}