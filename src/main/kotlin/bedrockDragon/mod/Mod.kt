package bedrockDragon.mod

import bedrockDragon.mod.informative.ModStatus

@DragonMod
abstract class Mod {

    //abstract val MODNAME: String
    //abstract val MODVERSION: Int
    abstract var Status: ModStatus
    /**
     * preInit should contain any blocks , entities, or items added.
     *
     * @author Nathan
     */
    abstract fun preInit()
    abstract fun init()
    abstract fun postInit()
}