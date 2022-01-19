package bedrockDragon.network.raknet.protocol.game.type.resourcepack

import kotlinx.serialization.Serializable

@Serializable
data class ResourceManifest(val format_version: Int,
                            val header: Header,
                            val modules: Array<Modules>) {

    @Serializable
    data class Header(val description: String,
                      val name: String,
                      val uuid: String,
                      val version: Array<Int>,
                      val min_engine_version: Array<Int>
    )

    @Serializable
    data class Modules(val description: String,
                       val type: String,
                       val uuid: String,
                       val version: Array<Int>
    )
}
