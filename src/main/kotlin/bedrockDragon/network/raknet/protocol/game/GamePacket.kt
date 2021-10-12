package bedrockDragon.network.raknet.protocol.game

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketConstants

class GamePacket: RakNetPacket(PacketConstants.GAME_PACKET) {
    private lateinit var body : RakNetPacket

    override fun encode() {

        write(*body.buffer().array())
    }

    override fun decode() {
        require(readByte().toInt() == PacketConstants.GAME_PACKET)

        body = read(buffer().array()) as RakNetPacket
    }
}