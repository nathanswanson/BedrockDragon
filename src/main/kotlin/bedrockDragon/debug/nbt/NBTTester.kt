package bedrockDragon.debug.nbt

import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import java.io.File

class NBTTester {
}

fun main() {
    val nbt = Nbt {
        variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
        compression = NbtCompression.None // None, Gzip, Zlib
        compressionLevel = null // in 0..9
        encodeDefaults = false
        ignoreUnknownKeys = false
        serializersModule = EmptySerializersModule
    }

    val file = File("players/test.nbt")
    val tag: NbtTag = file.inputStream().use { input ->
        nbt.decodeFromStream(input)
    }
}