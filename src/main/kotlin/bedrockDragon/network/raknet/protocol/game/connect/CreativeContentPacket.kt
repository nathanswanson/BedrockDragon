package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.collections.HashMap

class CreativeContentPacket: PacketPayload(MinecraftPacketConstants.CREATIVE_CONTENT) {

    lateinit var entries: HashMap<String, Int>

    @OptIn(ExperimentalSerializationApi::class)
    override fun encode() {

        writeUnsignedVarInt(0)
//        val stream = ClassLoader.getSystemResourceAsStream("creative_items.json")!!
//        val json = Json.decodeFromStream<Array<CreativeItemEntry>>(stream)
//
//        writeUnsignedVarInt(json.size)
//        json.forEach {
//            writeUnsignedVarInt(it.blockRuntimeId)
//        }
    }

    data class CreativeItemEntry(val id: String, val blockRuntimeId: Int)
}
