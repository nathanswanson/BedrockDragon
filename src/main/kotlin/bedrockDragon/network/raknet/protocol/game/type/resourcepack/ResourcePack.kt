package bedrockDragon.network.raknet.protocol.game.type.resourcepack

import bedrockDragon.network.raknet.protocol.game.type.resourcepack.ResourceManifest

abstract class ResourcePack{
    abstract val manifest: ResourceManifest
    abstract var packName: String
    abstract var packId: String
    abstract var packVersion: Array<Int>
    abstract var packSize: Int
    abstract var sha256: ByteArray
    abstract fun packChunk(start: Int, end: Int)
    fun verifyManifest(): Boolean {
        return true
    }
}