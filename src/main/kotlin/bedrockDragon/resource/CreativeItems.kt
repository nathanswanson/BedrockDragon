package bedrockDragon.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

object CreativeItems {
    @OptIn(ExperimentalSerializationApi::class)
    fun parse(): Array<CreativeEntry> {
        val runtimeJson = ClassLoader.getSystemResourceAsStream("creative_items.json")
        val sortedJson = Json.decodeFromStream<CreativeIt>(runtimeJson!!)
        return sortedJson.items
    }

    @Serializable
    data class CreativeIt(val items: Array<CreativeEntry>)

    @Serializable
    data class CreativeEntry(val id: String, val blockRuntimeId: Int)
}
