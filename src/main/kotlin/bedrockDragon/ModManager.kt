package bedrockDragon

import bedrockDragon.mod.Mod

object ModManager {
    private var modRegistry = ArrayList<Mod>()

    fun register(mod: Mod) {
        modRegistry.add(mod)
    }

    fun reload() {

    }
}