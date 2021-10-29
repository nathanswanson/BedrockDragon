package bedrockDragon.resource

import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.io.InputStream

object BiomeDefinitions {
    private val rawBiomeByteData: ByteArray

    init {
        val stream = ClassLoader.getSystemResourceAsStream("biome_definitions.dat")!!
        stream.use { bytes ->
            rawBiomeByteData = bytes.readBytes()
        }
    }

    fun PacketPayload.writeBiomeDefinition() {
        this.write(*rawBiomeByteData)
    }
}