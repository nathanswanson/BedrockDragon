package bedrockDragon.resource

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.VarInt
import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
object RuntimeItemState {
    var runtimeIdStates : Array<RuntimeEntry>

    var runtimeIdRegistry = HashMap<String, Int>()
    lateinit var binaryItemStatePayload : ByteArray
    init {
        val runtimeJson = ClassLoader.getSystemResourceAsStream("runtime_item_states.json")
        runtimeIdStates = Json.decodeFromStream(runtimeJson!!)
        runtimeIdStates.forEach{runtimeIdRegistry[it.name] = it.id}
        generateIdPayload()
    }
    fun getRuntimeIdFromName(name: String): Int {
        return runtimeIdRegistry[name] ?: -1
    }
    private fun generateIdPayload() {
        binaryItemStatePayload = Packet().let {
            it.writeUnsignedVarInt(runtimeIdStates.size)

            runtimeIdStates.forEach { state ->
                it.writeString(state.name)
                it.writeIntLE(state.id)
                it.writeBoolean(false)
            }
            it.buffer().array()
        }
    }
    @Serializable
    data class RuntimeEntry(val name: String, val id: Int)
}
