package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.registry.Registry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.collections.HashMap

class CreativeContentPacket: PacketPayload(MinecraftPacketConstants.CREATIVE_CONTENT) {

    lateinit var entries: HashMap<String, Int>

    @OptIn(ExperimentalSerializationApi::class)
    override fun encode() {
        writeUnsignedVarInt(Registry.ITEM_REGISTRY.size())

        for(i in Registry.ITEM_REGISTRY.allEntries()) {
            writeUnsignedVarInt(i.value.runtimeId)
            writeItem(i.value, true)
        }

    }
}
