package bedrockDragon.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

object RuntimeItemState {
    @OptIn(ExperimentalSerializationApi::class)
    fun parse(): Array<RuntimeEntry> {
        val runtimeJson = ClassLoader.getSystemResourceAsStream("runtime_item_states.json")
        val sortedJson = Json.decodeFromStream<Array<RuntimeEntry>>(runtimeJson!!)

        return sortedJson
    }

    @Serializable
    data class RuntimeEntry(val name: String, val id: Int)
}