package bedrockDragon.util

import bedrockDragon.network.raknet.protocol.game.type.ResourceManifest
import bedrockDragon.network.raknet.protocol.game.type.ResourcePack
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*
import java.util.zip.ZipFile

@OptIn(ExperimentalSerializationApi::class)
class ZippedResourcePack(val file: File): ResourcePack() {
                         override lateinit var manifest: ResourceManifest
                         override var packId: String = ""
                         override var packName: String = ""
                         override var packSize: Int = 0
                         override var packVersion: Array<Int> = emptyArray()
                         override var sha256: ByteArray = ByteArray(0)
    init {
        try {
            val zip = ZipFile(file)
            val entry = zip.getEntry("manifest.json")
            if(entry == null) {
                throw IllegalArgumentException("Resource pack attempted to load but has no manifest.")
            } else {
                manifest = Json.decodeFromStream(zip.getInputStream(entry))
                packId = manifest.header.uuid
                packName = manifest.header.name
                //TODO pack size and sha256
                packSize = 500
                packVersion = manifest.header.version
                sha256 = ByteArray(0)

            }
        } catch (e: IOException) {

        }
    }

    override fun packChunk(start: Int, end: Int) {
        TODO("Not yet implemented")
    }

}