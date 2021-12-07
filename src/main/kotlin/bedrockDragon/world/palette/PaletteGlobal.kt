package bedrockDragon.world.palette

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import java.util.*
import kotlin.collections.LinkedHashMap

object PaletteGlobal {



    @OptIn(ExperimentalSerializationApi::class)
    fun parse(): LinkedHashMap<String, Int>
    {
        val runtime = ClassLoader.getSystemResourceAsStream("block.json")
        return Json.decodeFromStream(runtime!!)
    }
}
