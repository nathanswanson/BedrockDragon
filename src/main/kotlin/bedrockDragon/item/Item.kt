package bedrockDragon.item

import bedrockDragon.command.Command
import bedrockDragon.command.CommandImpl
import bedrockDragon.player.Player
import bedrockDragon.registry.DSLBase
import bedrockDragon.registry.resource.VanillaItems
import bedrockDragon.resource.RuntimeItemState
import bedrockDragon.world.PaletteGlobal

@ItemDSL
sealed class Item(var name: String = "item"): DSLBase(){

    var runtimeId = -1 //todo const
    var maxStackSize = 64
    var subItems = mutableListOf<Item>()
    var tag: VanillaItems.ItemTag? = null
    var durability = -1
    var onActivate: ((Player) -> Unit)? = null
    var damage = -1

    //instance
    var iDurability = durability
    var count = 1

    override fun clone(): Item {
        return ItemImpl(name).let {
            it.runtimeId = runtimeId
            it.maxStackSize = maxStackSize
            it.subItems = subItems
            it.tag = tag
            it.durability = durability
            it.onActivate = onActivate
            it.damage = damage
            it
        }
    }
    @ItemDSL
    fun of(item: ItemImpl.() -> Unit) {
        val newItem = this.clone() as ItemImpl
        newItem.apply(item)
        //if the parent name has a * then concat parent and child name otherwise just use child name.
        if(name.contains("*")) {
            newItem.name = name.replace("*", newItem.name)
        }
        subItems.add(newItem)
    }
}

class ItemImpl(name: String): Item(name)

@ItemDSL
fun registerItem(modName: String, registerList: RegisterItem.() -> Unit) {
    RegisterItem(modName).run(registerList)
}


@ItemDSL
class RegisterItem(var modName: String) {

    @ItemDSL
    fun item(name: String, lambda: Item.() -> Unit = {}) {
        val item = ItemImpl(name).apply(lambda)
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