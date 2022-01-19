package bedrockDragon.item

import java.lang.Integer.min

fun item(item: Item.() -> Unit): Item {
    return Item().apply(item).build()
}

class Item(var name: String = "mod:item") {


    var runtimeId = -1 //todo const
    var maxStackSize = 1


    var count = 0
        set(value) {
            field = min(value, maxStackSize)
        }

    var damage = -1
        set(value) {
            if(damage > 0) //make sure item has durability by it not having damage-of -1.
                field = value
        }

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