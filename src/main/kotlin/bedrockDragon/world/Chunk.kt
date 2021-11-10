package bedrockDragon.world

import bedrockDragon.network.raknet.Packet
import com.curiouscreature.kotlin.math.Float3
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

//https://wiki.vg/Pocket_Minecraft_Map_Format
class Chunk()  {
    var blocks = ByteArray(32768)
    var data = ByteArray(16384)
    var skyLight = ByteArray(16384) { 0xff.toByte() }
    var blockLight = ByteArray(16384)
    var heightMap = Array<Int>(64) {256}
    var biomeColors = Array<Int>(256) {256}
    //var biomes = ByteArray(256)
        //var heightMap = ByteArray(256) { 256.toByte() }
    val position: Float3 = Float3(0f,0f,0f)

    fun subChunk() {

    }

    fun binary(): Packet {
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
        fun binary() {

        }
    }
}

