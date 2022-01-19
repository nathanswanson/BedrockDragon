package bedrockDragon.network.raknet.protocol.game.command

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.util.*

class CommandRequestPacket: PacketPayload(MinecraftPacketConstants.COMMAND_OUTPUT) {
    var command = "help"
    var commandType = -1 //vInt
    lateinit var uuid: UUID
    var requestId = ""
    var unknown = false

    override fun decode(packet: Packet) {
        command = packet.readString()
        commandType = packet.readVarInt()
        uuid = packet.readUUID()
        requestId = packet.readString()
        unknown = packet.readBoolean()
    }
}
