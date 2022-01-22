package bedrockDragon.item

import bedrockDragon.player.Player
import bedrockDragon.registry.VanillaItems
import java.lang.Integer.min


@ItemDSL
fun item(item: Item.() -> Unit): Item {
    return Item().apply(item).build()
}

@ItemDSL
data class Item(var name: String = "item") {


    var runtimeId = -1 //todo const
    var maxStackSize = 1
    var subItems = mutableListOf<Item>()
    var tag: VanillaItems.ItemTag? = null
    var durability = -1
    var onActivate: ((Player) -> Unit)? = null



    //instance
    var iDurability = durability

    var count = 0
        set(value) {
            field = min(value, maxStackSize)
        }

    var damage = -1

    @ItemDSL
    fun of(item: Item.() -> Unit) {
        val newItem = this.copy()
        newItem.apply(item)

        //if the parent name has a * then concat parent and child name otherwise just use child name.
        if(name.contains("*")) {
            newItem.name = name.replace("*", newItem.name)
        }
        subItems.add(newItem)
    }

    @ItemDSL
    fun build(): Item {
        return this
    }


    companion object {
        fun testItem(): Item {
            return Item().let {
                it.runtimeId = 145
                it.count = 1
                it
            }
        }
    }
}