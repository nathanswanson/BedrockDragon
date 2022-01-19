package bedrockDragon.util

import dev.romainguy.kotlin.math.Float2
import java.util.*


/**
 * World float2
 *
 * @constructor Create empty World float2
 */
class WorldInt2(var x: Int, var y: Int) {
    public constructor(float2: Float2): this(float2.x.toInt(), float2.y.toInt())

    fun float2() : Float2 {
        return Float2(x.toFloat(), y.toFloat())
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }

    override fun toString(): String {
        return "Position: [x: $x, y: $y]"
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }
}