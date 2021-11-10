package bedrockDragon.network.raknet.protocol.game.entity

import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AvaliableEntityIDPacket: PacketPayload() {
    override fun encode() {
        write(*javaClass.classLoader.getResourceAsStream("entity_identifiers.dat").readAllBytes())
    }
}