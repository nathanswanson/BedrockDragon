package bedrockDragon.mod

@DragonMod
abstract class Mod {

    abstract val modName: String

    /**
     * preInit should contain any blocks , entities, or items added.
     *
     * @author Nathan
     */
    abstract fun preInit()
    abstract fun init()
    abstract fun postInit()
}