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
        if(block.runtimeId == -1) {
            block.runtimeId = PaletteGlobal.getRuntimeIdFromName("$modName:${block.name}")
        } else {
            //PaletteGlobal.globalBlockPalette["$modName:${block.name}"] = block.runtimeId
            //todo create palette entry
        }
        PaletteGlobal.blockRegistry["$modName:${block.name}"] = block
    }
}