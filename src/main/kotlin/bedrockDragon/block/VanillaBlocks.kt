package bedrockDragon.block

import bedrockDragon.block.builder.registerBlock

object VanillaBlocks {
    init {
        registerBlock("minecraft") {
            block {
                name = "grass_block"
            }
            block {
                name = "air"
            }
            block {
                name = "bedrock"
            }
            block {
                name = "dirt"
            }
        }
    }
}