package bedrockDragon.resource

import bedrockDragon.network.raknet.protocol.game.type.ResourcePack
import bedrockDragon.util.ZippedResourcePack
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//nukit resourcePackManager
object ResourcePackManager {
    val path = File("resource_pack")
    private val resourcePacks : Array<ResourcePack>
    private val resourcePackId = HashMap<UUID, ResourcePack>()
    init {
        if(!path.exists()) {
            path.mkdir()
        }

        val loadedResourcePack = ArrayList<ResourcePack>()
        for(pack in path.listFiles()) {
            try {
                val resourcePack: ResourcePack

                resourcePack = ZippedResourcePack(pack)

                loadedResourcePack.add(resourcePack)

                //I shouldnt call from string on UUID
                resourcePackId[UUID.fromString(resourcePack.packId)] = resourcePack
            } catch (e: IllegalArgumentException) {
                //TODO
            }
        }

        resourcePacks = loadedResourcePack.toTypedArray()
    }
}