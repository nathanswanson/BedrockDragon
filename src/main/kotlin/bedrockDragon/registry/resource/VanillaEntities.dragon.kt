package bedrockDragon.registry.resource

import bedrockDragon.entity.registerEntity


object VanillaEntities {
    init {
        registerEntity("minecraft") {
            entity("minecraft:cow") {
                entityUniqueIdentifier = 11
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