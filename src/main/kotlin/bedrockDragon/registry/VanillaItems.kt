package bedrockDragon.registry

import bedrockDragon.item.registerItem

object VanillaItems {
    enum class ItemTag {
        AXE,
        SHOVEL,
        PICKAXE,
        SWORD,
        HOE
    }

    init {
        registerItem("minecraft") {
            item {
                name = "anvil"
            }
            item {
                name = "*_pickaxe"
                maxStackSize = 1
                tag = ItemTag.PICKAXE

                of {
                    name = "wooden"
                    durability = 59
                    damage = 2
                }
                of {
                    name = "stone"
                    durability = 131
                    damage = 3
                }
                of {
                    name = "iron"
                    durability = 250
                    damage = 4
                }
                of {
                    name = "golden"
                    durability = 32
                    damage = 2
                }
                of {
                    name = "diamond"
                    durability = 1561
                    damage = 5
                }
                of {
                    name = "netherite"
                    durability = 2031
                    damage = 6
                }
            }
            item {
                name = "*_sword"
                maxStackSize = 1
                tag = ItemTag.SWORD

                of {
                    name = "wooden"
                    durability = 60
                    damage = 5
                }
                of {
                    name = "stone"
                    durability = 132
                    damage = 5
                }
                of {
                    name = "iron"
                    durability = 251
                    damage = 6
                }
                of {
                    name = "golden"
                    durability = 33
                    damage = 7
                }
                of {
                    name = "diamond"
                    durability = 1562
                    damage = 8
                }
                of {
                    name = "netherite"
                    durability = 2032
                    damage = 9
                }
            }
        }
    }
}