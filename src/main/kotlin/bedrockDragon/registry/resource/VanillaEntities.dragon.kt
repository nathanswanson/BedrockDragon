package bedrockDragon.registry.resource

import bedrockDragon.entity.registerEntity
import bedrockDragon.util.aabb.AABB


object VanillaEntities {
    init {
        registerEntity("minecraft") {
            entity("minecraft:cow") {
                entityUniqueIdentifier = 11
                boundingBox = AABB(2f,1.5f,1.5f)
            }
            entity("minecraft:llama") {
                entityUniqueIdentifier = 29
            }
            entity("minecraft:axolotl") {
                entityUniqueIdentifier = 130
            }
        }
    }
}