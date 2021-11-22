package bedrockDragon.world

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.world.WorldInt2
import bedrockDragon.util.ISavable
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import net.benwoodworth.knbt.NbtCompoundBuilder
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.Path

//https://wiki.vg/Pocket_Minecraft_Map_Format
class Chunk(private val position: WorldInt2): ISavable  {

    override val fileName = Path("world/region/r.${position.x shr 5}.${position.y shr 5}.mca")
    override fun save(nbtBuilder: NbtCompoundBuilder) {
        TODO("Not yet implemented")
    }

    override fun read() {
        TODO("Not yet implemented")
    }

    var blocks = ByteArray(32768)
    var data = ByteArray(16384)
    var skyLight = ByteArray(16384) { 0xff.toByte() }
    var blockLight = ByteArray(16384)
    var heightMap = Array<Int>(64) {256}
    var biomeColors = Array<Int>(256) {256}

    fun subChunk() {

    }

    fun encode(): Packet {
        val outputStream = Packet()
        //outputStream.writeByte(0) //subchunk version
        outputStream.write(*blocks)
        outputStream.write(*data)
        outputStream.write(*skyLight)
        //skyLight.forEach {outputStream.writeUnsignedByte(it) }
        outputStream.write(*blockLight)
        heightMap.forEach { outputStream.writeInt(it) }
        biomeColors.forEach { outputStream.writeInt(it) }


        //extra data
        //outputStream.writeIntLE(0)//needs LE

        return outputStream
    }



    private class SubChunk {
        fun encode() {

        }
    }

    override fun toString(): String {
        return "Chunk pos: $position"
    }
}

