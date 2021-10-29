package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.resource.BiomeDefinitions.writeBiomeDefinition

class BiomeDefinitionPacket: PacketPayload() {

    override fun encode() {
        writeBiomeDefinition()
    }
}