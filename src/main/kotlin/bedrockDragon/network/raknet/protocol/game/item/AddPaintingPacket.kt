package bedrockDragon.network.raknet.protocol.game.item

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class AddPaintingPacket: PacketPayload(MinecraftPacketConstants.ADD_PAINTING) {
    var uniqueEntityId = -1L //sVarLong
    var runtimeEntityId = -1L //vLong
    lateinit var position: Float3
    var direction = -1 //sVarInt
    var name = PaintingType.Kebab

    override suspend fun encode() {
        writeVarLong(uniqueEntityId)
        writeUnsignedVarLong(runtimeEntityId)
        writeVector3(position)
        writeVarInt(direction)
        writeString(name.name)
    }

    enum class PaintingType {
        Kebab,
        Aztec,
        Alban,
        Aztec2,
        Bomb,
        Plant,
        Wasteland,
        Wanderer,
        Graham,
        Pool,
        Courbet,
        Sunset,
        Sea,
        Creebet,
        Match,
        Bust,
        Stage,
        Void,
        SkullAndRoses,
        Wither,
        Fighters,
        Skeleton,
        DonkeyKong,
        Pointer,
        PigScene,
        BurningSkull
    }
}