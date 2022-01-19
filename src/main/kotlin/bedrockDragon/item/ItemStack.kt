package bedrockDragon.item

import java.util.*

data class ItemStack(val item: Item, val stack: Int) {
    override fun hashCode(): Int {
        return Objects.hash(item, stack)
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}