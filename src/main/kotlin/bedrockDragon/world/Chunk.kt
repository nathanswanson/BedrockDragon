package bedrockDragon.world

import bedrockDragon.network.world.WorldInt2
import bedrockDragon.util.ISavable
import bedrockDragon.util.SaveStatus
import bedrockDragon.world.palette.PaletteSection
import dev.romainguy.kotlin.math.Float3
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import java.io.OutputStream
import kotlin.io.path.Path

//https://wiki.vg/Pocket_Minecraft_Map_Format
class Chunk(val position: WorldInt2,
            val parent: ChunkRelay?): ISavable  {


    //level


    var lastUpdate = 0L
    private var inhabitedTime = 0L
    private var isLightOn: Boolean = true
    private var status = "empty"

    private var sections = ArrayList<SubChunk>()
    private var fluidTicks = ArrayList<NbtTag>()
    private var postProcessing = ArrayList<NbtTag>()
    private var blockTicks = ArrayList<NbtTag>()
    private lateinit var heightMaps : NbtCompound
    private lateinit var structures : NbtCompound
    private var blockEntities = ArrayList<NbtTag>()

    override val fileName = Path("")
    var loadStatus = SaveStatus.EMPTY

    init {
        sections.add(SubChunk())
    }

    override fun save(nbtBuilder: NbtCompoundBuilder) {
        nbtBuilder.put("DataVersion", 2230)
        nbtBuilder.put("Level", buildNbtCompound{
            put("xPos", position.x)
            put("zPos", position.y)
            put("isLightOn", isLightOn)
            put("InhabitedTime", inhabitedTime)
            put("LastUpdate", lastUpdate)
            put("Status", status)
            //TileTicks
            //TileEntities
            //Sections
            //PostProcessing
            //LiquidTicks
            //Entities
            //Biomes
            //Structures
            //Heightmaps
        }) //Level

    }

    override fun read() {
        TODO("Not yet implemented")
    }

    fun encodePayload(): OutputStream {
        val stream = FastByteArrayOutputStream(1024)


        //block entities

        //sections
        sections.forEach {
            stream.write(it.encodePayload())
        }
        //biome array

        //border blocks
        stream.write(0)
        //block entities
        stream.write(0)


        return stream
    }

    fun encodeNbtToBinary(): ByteArray {
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Zlib // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }
        val builder = buildNbtCompound("") { save(this) }
        return nbt.encodeToByteArray(builder)
    }

    fun loadFromNbt() {
        val data = parent!!.parent!!.readChunkBinary(this)
        decodeFromNbt(data)
    }

    private fun decodeFromNbt(byteArray: ByteArray) {
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Zlib // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }

        val decodedNBT = nbt.decodeFromByteArray<NbtCompound>(byteArray)[""]!!.nbtCompound
        //todo add safety check for null
        //why is there x y and z pos for chunks...
        status = decodedNBT["Status"]!!.nbtString.value
        position.x = decodedNBT["xPos"]!!.nbtInt.value
        //position.y = decodedNBT["yPos"]!!.nbtInt.value.toFloat()
        position.y = decodedNBT["zPos"]!!.nbtInt.value
        lastUpdate = decodedNBT["LastUpdate"]!!.nbtLong.value
        inhabitedTime = decodedNBT["InhabitedTime"]!!.nbtLong.value
        isLightOn = decodedNBT["isLightOn"]!!.nbtByte.booleanValue


        decodedNBT["sections"]!!.nbtList.map {
           SubChunk.decodeFromNbt(it.nbtCompound)
        }.toList().toCollection(sections)
        decodedNBT["block_entities"]!!.nbtList.toCollection(blockEntities)
        heightMaps = decodedNBT["Heightmaps"]!!.nbtCompound
        decodedNBT["block_ticks"]!!.nbtList.toCollection(blockTicks)
        structures = decodedNBT["structures"]!!.nbtCompound
        decodedNBT["fluid_ticks"]!!.nbtList.toCollection(fluidTicks)
        decodedNBT["PostProcessing"]!!.nbtList.toCollection(postProcessing)

    }


    override fun toString(): String {
        return "Chunk pos: $position"
    }

    private class SubChunk {
        lateinit var blockStates : NbtCompound
        lateinit var biomes : NbtCompound
        lateinit var blockLight : NbtByteArray

        lateinit var paletteSection: PaletteSection
        var y: Byte = 0 //signed
        fun encodePayload(): ByteArray {
            val stream = FastByteArrayOutputStream(1024)
            stream.write(8)
            stream.write(2)
            //paletteSection.encode(stream)
            //storage
            return stream.array
        }

        companion object {
            fun decodeFromNbt(data: NbtCompound): SubChunk {
                val subChunk = SubChunk()
                subChunk.blockStates = data["block_states"]!!.nbtCompound
                subChunk.paletteSection = PaletteSection.parseBlockStateNBT(subChunk.blockStates)
                subChunk.biomes = data["biomes"]!!.nbtCompound
                subChunk.blockLight = data["BlockLight"]?.nbtByteArray ?: NbtByteArray(ByteArray(0))
                subChunk.y = data["Y"]!!.nbtByte.value

                return subChunk
            }
        }
    }
}


