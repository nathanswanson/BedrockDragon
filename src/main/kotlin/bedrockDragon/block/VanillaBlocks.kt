package bedrockDragon.block

import bedrockDragon.block.builder.registerBlock

object VanillaBlocks {
    init {
        registerBlock("minecraft") {
            block {
                name = "grass_block"
                hardness = 0.6
                blastResistance = 0.6
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