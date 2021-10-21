package bedrockDragon.world

import bedrockDragon.math.Vector3
import bedrockDragon.reactive.ReactSocket
import io.reactivex.rxjava3.core.Observable

class Chunk(override val observable: Observable<Any>) : ReactSocket<Any> {
    val position: Vector3 = TODO()

    fun subChunk() {

    }
}