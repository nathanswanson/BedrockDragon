package bedrockDragon.entity.living

import bedrockDragon.entity.DataTag
import bedrockDragon.entity.Entity
import bedrockDragon.entity.EntityDSL
import bedrockDragon.entity.effects.Effect
import bedrockDragon.inventory.ArmorInventory
import bedrockDragon.item.Item
import bedrockDragon.network.raknet.MetaTag
import bedrockDragon.network.raknet.protocol.game.entity.AddEntityPacket
import bedrockDragon.player.Player
import bedrockDragon.registry.Registry
import dev.romainguy.kotlin.math.Float3


@EntityDSL
class RegisterEntity(var modName: String) {
    @EntityDSL
    fun entity(name: String, lambda: Living.() -> Unit = {}) {
        val entity = Living(name).apply(lambda)

        Registry.ENTITY_REGISTRY[name] = entity
    }
}

@EntityDSL
fun registerEntity(modName: String, registerList: RegisterEntity.() -> Unit) {
    RegisterEntity(modName).run(registerList)
}

open class Living(override var name: String): Entity() {
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
    open var health: Float = 1f
    var hurtByTimestamp: Int = 0
    var hurtTime: Short = 0
    var handDropChances: Array<Float>? = null //2
    var handItems: Array<Item>? = null //2
    var leash: Item? = null //TODO
    var leftHanded: Boolean = false
    var noAi: Boolean? = null
    var persistenceRequired: Boolean? = null
    var sleepingPos: Float3? = null

    //physics
    private var lastPosition = position
    var velocity = Float3(0f,0f,0f)
    private var fallStartPosition = position

    protected var attributes = MetaTag()

    fun getDrops(): List<Item> {return emptyList()}
    fun armor(): ArmorInventory {return armor()}

    init {
        var flag = 0L xor (1L shl DataTag.DATA_FLAG_GRAVITY)
        flag = flag xor (1L shl DataTag.DATA_FLAG_BREATHING)
        flag = flag xor (1L shl DataTag.DATA_FLAG_HAS_COLLISION)


        //for testing
        attributes.put(DataTag.DATA_FLAGS, MetaTag.TypedDefineTag.TAGLONG(flag))
        attributes.put(DataTag.DATA_FLAG_ALWAYS_SHOW_NAMETAG, MetaTag.TypedDefineTag.TAGBYTE(1))
        attributes.put(DataTag.DATA_SCALE, MetaTag.TypedDefineTag.TAGFLOAT(1f))
        attributes.put(DataTag.DATA_BOUNDING_BOX_HEIGHT, MetaTag.TypedDefineTag.TAGFLOAT(/*boundingBox.height*/1.9f))
        attributes.put(DataTag.DATA_BOUNDING_BOX_WIDTH, MetaTag.TypedDefineTag.TAGFLOAT(/*boundingBox.width*/0.75f))
        attributes.put(DataTag.DATA_HEALTH, MetaTag.TypedDefineTag.TAGINT(health.toInt()))
        attributes.put(DataTag.DATA_LEAD_HOLDER_EID, MetaTag.TypedDefineTag.TAGLONG(-1L))
    }

    open fun kill() {
        //todo
    }

    open fun getAttributes() {

    }

    override fun showFor(players: List<Player>) {
        players.forEach { player ->
            player.nettyQueue.add(
                AddEntityPacket().let {
                    it.entitySelfId = this.entityUniqueIdentifier
                    it.runtimeEntityId = runtimeEntityId
                    it.position = position
                    it.entityType = name
                    it.metaData = attributes
                    it.gamePacket()
                }
            )
        }
    }

    override fun update() {
        super.update()
        velocity = position - lastPosition
        lastPosition = position
        if(!(this is Player && gamemode == Player.Gamemode.CREATIVE)) {
            if(velocity.y < -0.05 && onGround)
            {
                fallStartPosition = position
                onGround = false
            } else if(velocity.y >= 0 && !onGround) {
                //hit ground
                onGround = true
                fallDistance = fallStartPosition.y - position.y
                if(fallDistance-3 > 0)
                    damage(fallDistance-3)
            }
        }
    }

    open fun damage(amount: Float) {
        health-=amount

        if(health < 0) {
            kill()
        }
    }
}