package bedrockDragon.network.raknet.protocol.game.player

import bedrockDragon.item.Item
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
    lateinit var rotation: Float2
    lateinit var heldItem: Item
    //metadata TODO
    var flags = -1 //varInt
    var commandPermissions = -1 //varInt
    var customStoredPermissions = -1 //varInt
    var userId = -1
    //Links TODO
    var deviceId = ""
    var deviceOS = -1

    override fun encode() {
        super.encode()
    }
}