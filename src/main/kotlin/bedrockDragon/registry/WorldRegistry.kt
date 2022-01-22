package bedrockDragon.registry

import bedrockDragon.world.World

object WorldRegistry {
    private val worlds = HashMap<Int, World>()

    fun register(id: Int, world: World): Boolean {
        return worlds.putIfAbsent(id, world) == null
    }

    fun getWorld(id: Int): World? {
        return worlds[id]
    }
}