package bedrockDragon.block

import bedrockDragon.Item
import bedrockDragon.block.builder.BlockDSL
import bedrockDragon.block.tileEntity.Tile
import bedrockDragon.player.Player

@BlockDSL
fun block(block: Block.() -> Unit): Block {
    return Block().apply(block).build()
}

@BlockDSL
class Block(var name: String = "mod:block") {

    //this will be provided by the builder constructor

    var stackSize = 64
    var breakTool: Item? = null
    var blastResistance = 0.0
    var hardness = 0.0
    var luminant = false
    var transparent = false
    var flammable = false
    var drops = emptyArray<Item>()
    var gravity = GravityEffect.FLOAT
    var tileEntity: Tile? = null

    //todo bounding box
    //todo texture tag
    //todo recipe builder
    //todo runtimeId assign
    //events

    var onInteract: ((Player) -> Unit)? = null
    var onNearbyBlockChanged: (() -> Unit)? = null
    var onRandomTick: (() -> Unit)? = null
    var onTick: (() -> Unit)? = null
    var onBlockAdded: (() -> Unit)? = null

    fun build(): Block {
        return this
    }
    enum class GravityEffect {
        FLOAT,
        FALL,
        BREAK
    }

    override fun toString(): String {
        return """Block: $name"""
    }

}
