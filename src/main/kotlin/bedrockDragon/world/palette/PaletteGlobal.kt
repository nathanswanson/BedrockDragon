package bedrockDragon.world.palette

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import java.util.*
import kotlin.collections.LinkedHashMap

object PaletteGlobal {

    val globalBlockPalette = LinkedHashMap<String, Int>()

    init
    {
        val runtime = ClassLoader.getSystemResourceAsStream("blocks.json")
        Json.decodeFromStream<JsonObject>(runtime!!).map { globalBlockPalette.put(it.key, it.value.jsonObject["states"]!!.jsonArray[0].jsonObject["id"]!!.jsonPrimitive.int) }
    }
}

fun main() {
    println(PaletteGlobal.globalBlockPalette)
}
