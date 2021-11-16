package bedrockDragon.world

import com.curiouscreature.kotlin.math.Float2

fun WorldInt2.toChunkSpace() {
    x = x shr 4
    y = y shr 4
}

fun WorldInt2.toChunkRelaySpace() {
    x = x shr 6
    y = y shr 6
}