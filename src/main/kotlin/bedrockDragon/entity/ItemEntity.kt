package bedrockDragon.entity

import bedrockDragon.item.Item
import bedrockDragon.util.aabb.AABB
import dev.romainguy.kotlin.math.Float3

class ItemEntity(val item: Item): Entity() {

    init {
        boundingBox = AABB(0f,0f,0f)
        name = item.name
    }
}