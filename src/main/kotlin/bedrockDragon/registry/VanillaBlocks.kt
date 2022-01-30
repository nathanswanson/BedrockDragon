package bedrockDragon.registry

import bedrockDragon.block.Block
import bedrockDragon.block.builder.registerBlock
import bedrockDragon.inventory.BlockInventory
import bedrockDragon.inventory.Inventory

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
            block {
                name = "stone"
                hardness = 0.6
            }
            block {
                name = "anvil"
                gravity = Block.GravityEffect.FALL
            }
            block {
                name = "chest"
                inventory = BlockInventory(27)
                onInteract = {
                    it.openInventory(inventory as BlockInventory)
                }
            }
        }
    }
}