package bedrockDragon.entity

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.entity.AddItemEntityPacket
import bedrockDragon.network.raknet.protocol.game.entity.RemoveEntityPacket
import bedrockDragon.network.raknet.protocol.game.entity.TakeItemEntityPacket
import bedrockDragon.player.Player
import bedrockDragon.reactive.RemoveEntity
import bedrockDragon.reactive.TakeItem
import bedrockDragon.util.aabb.AABB
import dev.romainguy.kotlin.math.Float3

class ItemEntity(val item: Item): Entity() {

    init {
        boundingBox = AABB(0f,0f,0f)
        name = item.name
    }

    private var dirty = false

    override fun showFor(players: List<Player>) {
        if(players.isNotEmpty()) {
            val packet = AddItemEntityPacket().let {
                it.runtimeId = runtimeEntityId
                it.item = item
                it.pos = position
                it.entityIdSelf = runtimeEntityId
                it.isFromFishing = false
                it.velocity = Float3(0f,0f,0f)//todo
                it.gamePacket()
            }
            players.forEach { player ->
                player.nettyQueue.add(packet)
            }
        }
    }

    override fun handleIntersection(otherEntity: Entity) {
        if(!dirty) {
            dirty = true
            chunkRelay.removeEntity(this)

            if(otherEntity is Player) {
                otherEntity.chunkRelay.invoke(TakeItem(TakeItemEntityPacket().let {
                    it.runtimeId = otherEntity.runtimeEntityId
                    it.target = runtimeEntityId
                    it }, otherEntity))

                otherEntity.addItem(this.item)
            }

        }
    }
}