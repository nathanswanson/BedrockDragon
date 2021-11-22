package bedrockDragon.inventory

import bedrockDragon.Item
import bedrockDragon.player.Player

abstract class Inventory(size: Int) {
    private val viewers: Array<Player> = emptyArray()
    private val slots: Array<Item?> = arrayOfNulls<Item>(size)
    var type = -1
    val windowId = 0

    fun contains(item: Item) {
        slots.contains(item)
    }

    fun addItem(item: Item): Boolean {
        return try {
            slots[slots.indexOfFirst { it == null }]
            true
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    fun addItem(item: Item, slot: Int): Boolean {
        if(slots[slot] == null) {
            slots[slot] = item
            return true
        }
        return false
    }

    fun forceAdd(item: Item, slot: Int): Boolean {
        slots[slot] = item
        return true
    }

    fun clear() {
        slots.fill(null)
    }

    abstract fun openInventory()
}