package bedrockDragon.util

import net.benwoodworth.knbt.*
import java.io.File
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

interface ISavable {
    val fileName: Path
    fun save(nbtBuilder: NbtCompoundBuilder)
    /**
     * Read reads entire nbt file as puts it into memory
     *
     *
     */
    fun read()

}