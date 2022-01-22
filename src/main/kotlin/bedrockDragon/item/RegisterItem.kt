package bedrockDragon.item

import bedrockDragon.resource.RuntimeItemState
import bedrockDragon.world.PaletteGlobal


@ItemRegistryDSL
fun registerItem(modName: String, registerList: RegisterItem.() -> Unit) {
    RegisterItem(modName).run(registerList)
}


@ItemRegistryDSL
class RegisterItem(var modName: String) {

    @ItemRegistryDSL
    fun item(lambda: Item.() -> Unit = {}) {
        val item = Item().apply(lambda)
        if(item.subItems.isNotEmpty()) {
            item.subItems.forEach {
                it.name = "$modName:${it.name}"
                PaletteGlobal.itemRegistry[it.name] = it
                ensureRuntimeIdAlloc(it)
            }
        } else {
            item.name = "$modName:${item.name}"
            PaletteGlobal.itemRegistry[item.name] = item
            ensureRuntimeIdAlloc(item)
        }
    }

    private fun ensureRuntimeIdAlloc(item: Item) {
        if(item.runtimeId == -1) {
            item.runtimeId = RuntimeItemState.getRuntimeIdFromName(item.name)
        } else {
            //todo
        }
    }
}