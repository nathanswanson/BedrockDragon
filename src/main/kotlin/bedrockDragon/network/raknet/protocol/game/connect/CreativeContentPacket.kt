package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.registry.Registry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.FileReader
import kotlin.collections.HashMap

class CreativeContentPacket: PacketPayload(MinecraftPacketConstants.CREATIVE_CONTENT) {

    lateinit var entries: HashMap<String, Int>

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun encode() {
//        writeUnsignedVarInt(0)
//        val stream = ClassLoader.getSystemResourceAsStream("creative_items.json")
//        val jData = Json.decodeFromStream<JsonObject>(stream)["items"]
//        for(i in jData as JsonArray) {
//            val element = (i as JsonObject)
//            if(element.containsKey("blockRuntimeId") && element.containsKey("id")) {
//                if(Registry.ITEM_REGISTRY.containsKey(element["id"]!!.jsonPrimitive.content)) {
//                    writeUnsignedVarInt(element["blockRuntimeId"]!!.jsonPrimitive.int)
//                    writeItem(Registry.ITEM_REGISTRY[element["id"]!!.jsonPrimitive.content], true)
//                }
//            }
//        }
        //demo
        writeUnsignedVarInt(2)

        writeUnsignedVarInt(6891)
        writeItem(Registry.ITEM_REGISTRY["minecraft:grass"], true)

        writeUnsignedVarInt(267)
        writeItem(Registry.ITEM_REGISTRY["minecraft:deepslate"], true)

    }
}
