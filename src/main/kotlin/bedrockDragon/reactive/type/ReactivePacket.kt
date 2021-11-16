package bedrockDragon.reactive.type

import com.curiouscreature.kotlin.math.Float3

abstract class ReactivePacket<T>(val payload: T) {
    val tags = emptyArray<Any>()
    val priority = 0
}

class MovePlayer(payload: Float3) : ReactivePacket<Float3>(payload)
class AnimatePlayer()
class RotatePlayer(payload: Float3) : ReactivePacket<Float3>(payload)
