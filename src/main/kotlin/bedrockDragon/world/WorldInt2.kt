package bedrockDragon.world

import com.curiouscreature.kotlin.math.Float2


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

    override fun toString(): String {
        return "Position: [x: $x, y: $y]"
    }
}