/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.world.region

import bedrockDragon.util.SaveStatus
import bedrockDragon.world.World
import bedrockDragon.world.chunk.Chunk
import bedrockDragon.world.chunk.ChunkRelay
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

/**
 * Region represents Minecrafts mcRegion format for Anvil.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Region(val x : Int,val z: Int,val world: World): Iterable<Chunk> {
    val fileName = Path("${world.name}/region/r.$x.$z.mca")
    val logger = KotlinLogging.logger {}
    val relayGrid = arrayOfNulls<ChunkRelay>(64) //make private is public atm for testing
    var manifest: RegionManifest = RegionManifest(fileName)

    init {
        //make sure region file exists
        if (!Path("${world.name}/region").exists()) {
            Path("${world.name}/region").createDirectory()
        }
        //load or create region file
        if (!fileName.exists()) {
            fileName.createFile()
            fileName.writeBytes(ByteArray(8192) {0})
        } else {

        }
    }

    /**
     * [getRelayAt] uses relative position from region to find a relay. If one does not exist is creates it.
     *
     */
    fun getRelayAt(x: Int, z: Int): ChunkRelay {
        if(x < 0 || z < 0)
            throw IllegalArgumentException("Cannot get relay with negative index at x:$x and/or z:$z")
        val relativePos = (x shl 3) + z
        if (relayGrid[relativePos] == null) {
            relayGrid[relativePos] = ChunkRelay(x, z, this)
        }
        return relayGrid[relativePos]!!
    }

    /**
     * [findChunkPointer] takes [idxLocationTable] and reads the bytes at this address. these bytes represent the
     * start and end of the memory segment fo the given chunk.
     */
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

    /**
     * [idxLocationTable] takes a chunk coordinates (relative to the region) then returns the memory
     * address/position of that chunk in the .mca file
     */
    private fun idxLocationTable(x: Int, z: Int): Long {
        return (((x and 31) + (z and 31) * 32) shl 2).toLong()
    }

    /**
     * [readTryte] takes 3 bytes from stream and decodes an int.
     */
    private fun readTryte(inputStream: InputStream): Int {
        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()
    }

    /**
     * [readTryte] takes 3 bytes from stream and decodes an int.
     */
    private fun readTryte(inputStream: RandomAccessFile): Int {
        return (inputStream.read() shl 16) +
                (inputStream.read() shl 8) +
                inputStream.read()
    }

    /**
     * [writeTryte] takes an int and writes it as 3 bytes to the outputStream.
     * overflow is not thrown instead it is maxed at 0xFFFFFF
     */
    private fun writeTryte(value: Int, outputStream: RandomAccessFile) {
        outputStream.write((value shr 16) and 0xFFFFFF)
        outputStream.write((value shr 8)and 0xFFFFFF)
        outputStream.write(value)
    }

    /**
     * [writeRegion] writes the entire .mca file do not use this if only saving one chunk.
     *
     */
    fun writeRegion() {
        val writer = RandomAccessFile(fileName.toFile(), "rw")
        iterator().forEach {

            if (it.loadStatus != SaveStatus.EMPTY) {
                val chunkData = it.encodeNbtToStorage()
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

    /** [readRegion] reads the entire .mca file however, it is not used and not implemented yet. */
    fun readRegion() {
        TODO("Not yet implemented")
    }

    /**
     * [writeChunkBinary] writes the given chunk its designated region file.
     * This method tries to save in original position however if it has
     * grown into a new region section it most likely will be moved
     * to the end of the file.
     */
    fun writeChunkBinary(chunk: Chunk) {
        RandomAccessFile(fileName.toFile(), "rw").use {
            val location = idxLocationTable(chunk.position.x, chunk.position.y)
            it.seek(location)
            val chunkBinary = chunk.encodeNbtToStorage()

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

    /**
     *
     */
    fun readChunkBinary(chunk: Chunk): ByteArray {
        val regionPointer = findChunkPointer(chunk.position.x, chunk.position.y)
        RandomAccessFile(fileName.toFile(), "r").use {
            it.seek(regionPointer!!.start.toLong() * 4096 + 5)
            val tempArray = ByteArray(regionPointer!!.end * 4096)
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

    override fun toString(): String {
        return "Region: x:$x z:$z"
    }
    data class RegionPointer(val start: Int, val end: Int)

    /**
     * Region Iterator traverses all 1024 chunks in the given region. This really should only be used for
     * debugging.
     */
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