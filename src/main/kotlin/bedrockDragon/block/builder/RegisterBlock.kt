package bedrockDragon.block.builder

import bedrockDragon.block.Block
import bedrockDragon.world.PaletteGlobal

@BlockRegistryDSL
fun registerBlock(modName: String, registerList: RegisterBlock.() -> Unit) {
    RegisterBlock(modName).run(registerList)
}


@BlockRegistryDSL
class RegisterBlock(var modName: String) {

    @BlockRegistryDSL
    fun block(lambda: Block.() -> Unit = {}) {
        val block = Block().apply(lambda)
        PaletteGlobal.blockRegistry["$modName:${block.name}"] = block
    }
}