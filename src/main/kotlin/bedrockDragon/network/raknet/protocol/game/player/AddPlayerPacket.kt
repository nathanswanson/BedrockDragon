package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.MetaTag
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import java.util.*

class AddPlayerPacket: PacketPayload(MinecraftPacketConstants.ADD_PLAYER) {
    lateinit var uuid: UUID
    var username = ""
    var entitySelfId = -1L //sVarLong
    var runtimeEntityId = -1L //varLong
    var platformChatId = ""
    lateinit var position: Float3
    lateinit var velocity: Float3
    lateinit var rotation: Float3
    lateinit var heldItem: Item
    lateinit var metaTag: MetaTag
    var flags = -1 //varInt
    var commandPermissions = -1 //varInt
    var customStoredPermissions = -1 //varInt
    var userId = -1
    //Links TODO
    var deviceId = ""
    var deviceOS = -1

    override fun encode() {
        writeUUID(uuid)
        writeString(username)
        writeVarLong(entitySelfId)
        writeUnsignedVarLong(runtimeEntityId)
        writeString(platformChatId)
        writeVector3(position)
        writeVector3(velocity)
        writeVector3(rotation)
        writeItem(heldItem)
        writeMetaData(metaTag)
        writeUnsignedVarInt(0) //adventure
        writeUnsignedVarInt(0)
        writeUnsignedVarInt(0)
        writeUnsignedVarInt(0)
        writeUnsignedVarInt(0)
        writeLongLE(entitySelfId)
        writeUnsignedVarInt(0) //links
        writeString(deviceId)
        writeIntLE(deviceOS)
    }
}