package bedrockDragon.entity.living

import bedrockDragon.Item
import bedrockDragon.entity.Entity
import bedrockDragon.entity.effects.Effect
import bedrockDragon.inventory.ArmorInventory
import dev.romainguy.kotlin.math.Float3

abstract class Living: Entity() {
    var absorptionAmount: Float = 0f
    var activeEffects: ArrayList<Effect>? = null
    var armorDropChances: Array<Float>? = null //4 one for each armor piece
    var armorItems: Array<Item>? = null //4 for each armor slot
    //var attributes
    //var brain
    var canPickUpLoot: Boolean = false
    var deathLootTable: String? = null
    var deathLootTableSeed: Long? = null
    var deathTime: Short = 0
    var fallFlying: Byte = 0
    var health: Float = 0f
    var hurtByTimestamp: Int = 0
    var hurtTime: Short = 0
    var handDropChances: Array<Float>? = null //2
    var handItems: Array<Item>? = null //2
    var leash: Item? = null //TODO
    var leftHanded: Boolean = false
    var noAi: Boolean? = null
    var persistenceRequired: Boolean? = null
    var sleepingPos: Float3? = null


    abstract fun getDrops(): List<Item>
    abstract suspend fun tick()
    abstract fun armor(): ArmorInventory
    open fun kill() {
        print("todo")
    }
    fun damage(amount: Float) {
        health-=amount
        if(health < 0) {
            kill()
        }
    }



    fun setHealth() {

    }
}