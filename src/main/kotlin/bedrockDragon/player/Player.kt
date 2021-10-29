package bedrockDragon.player

import bedrockDragon.entity.living.Living
import bedrockDragon.inventory.ArmorInventory
import bedrockDragon.item.Item
import java.util.*

class Player: Living() {
    var gamemode = Gamemode.SURVIVAL
    var isOp = false
    var name = "Nathan"
    val runtimeEntityId: ULong = /*UUID.randomUUID().mostSignificantBits.toULong()*/ 1000u
    val entityIdSelf: Long = /*runtimeEntityId.toLong()*/ 1000

    override fun getDrops(): List<Item> {
        return emptyList()
    }

    override fun getHealth(): Float {
        return 0f
    }

    override fun tick() {
    }

    override fun armor(): ArmorInventory {
        return ArmorInventory()
    }

    override fun damage() {
    }

    enum class Gamemode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }
}