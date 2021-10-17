package bedrockDragon.network.raknet.protocol.game

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.VarInt

class GamePacket(decompressed: ByteArray) : Packet() {
    val header: Int
    val gamePacketId: Int
    val gamePacketContent: ByteArray
    init {
        val stream = decompressed.inputStream()
        header = VarInt.readUnsignedVarInt(stream).toInt()
        gamePacketId = stream.read()
        gamePacketContent = stream.readBytes()
        stream.close()


    }
}