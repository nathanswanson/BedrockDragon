package bedrockDragon.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import java.util.*
import kotlin.collections.LinkedHashMap

object RuntimeBlocks {
    @OptIn(ExperimentalSerializationApi::class)
    fun parse(): LinkedHashMap<String, Int>
    {
        val runtime = ClassLoader.getSystemResourceAsStream("block_id.json")
        return Json.decodeFromStream(runtime)
    }
}
