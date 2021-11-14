package bedrockDragon.entity.living

import bedrockDragon.Item
import bedrockDragon.entity.Entity
import bedrockDragon.inventory.ArmorInventory

abstract class Living: Entity() {
    abstract fun getDrops(): List<Item>
    abstract fun getHealth(): Float
    abstract fun tick()
    abstract fun armor(): ArmorInventory
    abstract fun damage()

    fun setHealth() {

    }
}