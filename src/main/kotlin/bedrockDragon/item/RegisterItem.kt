package bedrockDragon.item

@ItemRegistryDSL
fun registerBlock(modName: String, registerList: RegisterBlock.() -> Unit) {
    RegisterBlock(modName).run(registerList)
}


@ItemRegistryDSL
class RegisterBlock(var modName: String) {

    @ItemRegistryDSL
    fun block(lambda: Item.() -> Unit = {}) {
        val block = Item().apply(lambda)
            //PaletteGlobal.blockRegistry["$modName:${block.name}"] = block
    }
}