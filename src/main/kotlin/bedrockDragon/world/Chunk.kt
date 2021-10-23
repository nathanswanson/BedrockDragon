package bedrockDragon.world

import bedrockDragon.math.Vector3
import bedrockDragon.math.Vector3Int
import bedrockDragon.reactive.ReactSocket
import io.reactivex.rxjava3.core.Observable

class Chunk(override val observable: Observable<Any>) : ReactSocket<Any> {
    val position: Vector3Int = TODO()

    fun subChunk() {

    }
}