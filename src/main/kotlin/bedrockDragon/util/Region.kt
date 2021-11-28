package bedrockDragon.util

import bedrockDragon.util.nbt.RegionManifest
import bedrockDragon.world.Chunk
import bedrockDragon.world.ChunkRelay
import kotlinx.serialization.modules.EmptySerializersModule
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import java.io.InputStream
import java.io.RandomAccessFile
import kotlin.io.path.*
import kotlin.math.ceil

/*
McRegion format 32x32 chunk save file (1024 chunks)

I. (Structure)
    a. Location Table (4KB)

    1024 entries (4 Bytes).
    First three bytes are memory position in file.
    Last byte is size of chunk.

    b. Timestamp Table (4KB)

    1024 entries (4 Bytes).
    Four bytes are integer of time chunk was last modified.

    c. Chunk Header (...KB)

    each chunk contains 5 byte header
    Four bytes for length
    One byte for compression (use 1 for gzip)

II. (Chunk)
    Following every Chunk header is chunk data in the NBT Format compressed with gzip.

*/


fun main() {
    val region = Region(0,0)
    region.manifest.readManifest()
    val relay = region.getRelayAt(1,1)
    val chunk = relay.chunks[0]
    chunk.loadFromNbt()

}

class Region(val x : Int,val z: Int): Iterable<Chunk> {
    val fileName = Path("world/region/r.$x.$z.mca")
    val logger = KotlinLogging.logger {}
    val relayGrid = arrayOfNulls<ChunkRelay>(64) //make private is public atm for testing
    var manifest: RegionManifest = RegionManifest(fileName)


    init {
        //make sure region file exists
        if (!Path("world/region").exists()) {
            Path("world/region").createDirectory()
        }
        //load or create region file
        if (!fileName.exists()) {
            fileName.createFile()
            fileName.writeBytes(ByteArray(8192) {0})
        } else {

        }
    }

    //chunk relay is 4x4 chunks so 8x8 chunk relays should be a region

    fun getRelayAt(x: Int, z: Int): ChunkRelay {
        val relativePos = (x shl 3) + z
        //println(z + (this.z shl 5))
        if (relayGrid[relativePos] == null) {

            relayGrid[relativePos] = ChunkRelay(x + (this.x shl 5), z + (this.z shl 5), this)
            //   println(relayGrid[relativePos])
        }
        return relayGrid[relativePos]!!
    }

    private fun findChunkPointer(x: Int, z: Int): RegionPointer? {
        val reader = fileName.inputStream()
        //(x % 32 + z % 32) * 32) * 4
        // (x and 31 + z and 31) shl 7
        reader.skip(idxLocationTable(x, z))

        val xPointer = readTryte(reader)
        val yPointer = reader.read()
        if ((xPointer == 0) and (yPointer == 0)) {
            return null
        }
        return RegionPointer(xPointer, yPointer)
    }

    fun readChunkBinary(chunk: Chunk): ByteArray {
        val regionPointer = RegionPointer(chunk.position.x, chunk.position.y)
        RandomAccessFile(fileName.toFile(), "r").use {
            it.seek(regionPointer.start.toLong() * 4096 + 5)
            val tempArray = ByteArray(regionPointer.end * 4096)
            it.read(tempArray)


            val nbt = Nbt {
                variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
                compression = NbtCompression.Zlib // None, Gzip, Zlib
                compressionLevel = null // in 0..9
                encodeDefaults = true
                ignoreUnknownKeys = true
                serializersModule = EmptySerializersModule
            }

            //val nbtData = nbt.decodeFromByteArray<NbtCompound>(tempArray)[""]!!.nbtCompound
            //val chunk = Chunk(WorldInt2(nbtData["xPos"].nbtInt.v, nbtData["zPos"]!!.nbtInt.value), TODO())
            return tempArray
        }
    }

    private fun idxLocationTable(x: Int, z: Int): Long {

        return (((x and 31) + (z and 31) * 32) shl 2).toLong()
    }

    private fun readTryte(inputStream: InputStream): Int {
        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()
    }

    private fun readTryte(inputStream: RandomAccessFile): Int {
        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()
    }

    private fun writeTryte(value: Int, outputStream: RandomAccessFile) {
        outputStream.write(value shr 16)
        outputStream.write(value shr 8)
        outputStream.write(value)
    }

    fun save() {
        val writer = RandomAccessFile(fileName.toFile(), "rw")
        iterator().forEach {

            if (it.loadStatus != SaveStatus.EMPTY) {
                val chunkData = it.encodeNbtToBinary()
                val locationPosition = idxLocationTable(it.position.x, it.position.y)
                writer.seek(locationPosition)


                val chunkPointer = readTryte(writer).toLong()
                val chunkEndOffset = writer.readByte()
                val newOffsetDemand = ceil(chunkData.size / 4096.0).toInt()

                if(chunkPointer == 0L || newOffsetDemand > chunkEndOffset) {
                    //location map
                    writer.seek(locationPosition)
                    val newOffset = manifest.getFirstUsableSlot(newOffsetDemand)
                    writeTryte(newOffset, writer)
                    writer.writeByte(newOffsetDemand)

                    //timestamp
                    writer.seek(locationPosition + 4096L)
                    writer.writeLong(it.lastUpdate)
                    writer.seek((newOffset + 1) * 4096L)


                } else {
                    //timestamp
                    writer.seek(locationPosition + 4096L)
                    writer.writeLong(it.lastUpdate)
                    writer.seek((chunkPointer + 1) * 4096L)
                }

                //chunk header
                writer.writeLong(chunkData.size.toLong()) //size
                writer.write(1) //gzip
                writer.write(chunkData)
            } else {
                //empty so write
                val locTable = idxLocationTable(it.position.x, it.position.y)
                writer.seek(locTable)
                writer.write(byteArrayOf(0,0,0,0))
                writer.seek(locTable + 4096)
                writer.write(byteArrayOf(0,0,0,0))
            }
        }
        writer.close()
    }

    //tries to save in original position however if it has grown into a new region section it most likely will be moved
    //to the end of the file
    fun saveChunk(chunk: Chunk) {
        RandomAccessFile(fileName.toFile(), "rw").use {
            val location = idxLocationTable(chunk.position.x, chunk.position.y)
            it.seek(location)
            val chunkBinary = chunk.encodeNbtToBinary()

            //check if chunk exists or needs a bigger space
            val chunkPointer = readTryte(it).toLong()
            val chunkEndOffset = it.readByte()
            val newOffsetDemand = ceil(chunkBinary.size / 4096.0).toInt()

            if(chunkPointer == 0L || newOffsetDemand > chunkEndOffset) {
                //non assigned chunk
                logger.info { "non loaded"  }
                it.seek(location)
                writeTryte(manifest.getFirstUsableSlot(newOffsetDemand), it)
                it.writeByte(newOffsetDemand)
            } else {
                //assign time and also new chunkheader/chunk
                it.seek(chunkPointer * 4096 + 8192)
                //chunk header
                it.writeLong(chunkBinary.size.toLong())
                it.write(1)
                it.write(chunkBinary)
            }
        }
    }

    fun read() {
        TODO("Not yet implemented")
    }

    data class RegionPointer(val start: Int, val end: Int)

    override fun iterator(): Iterator<Chunk> {

        return object: Iterator<Chunk> {

            //32x32 region chunk
            //8x8 relays
            //4x4 chunks

            private var chunkReadPtr = 0

            override fun hasNext(): Boolean {
                return chunkReadPtr < (32 * 32)
            }

            override fun next(): Chunk {
                return getRelayAt((chunkReadPtr shr 2) and 7, (chunkReadPtr shr 7)).chunks[((chunkReadPtr and 3) shl 2) + ((chunkReadPtr++ shr 5) and 3)]
            }
        }
    }
}