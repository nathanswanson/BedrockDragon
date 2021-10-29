package bedrockDragon.world

import bedrockDragon.reactive.ReactSocket
import com.curiouscreature.kotlin.math.Float3
import io.reactivex.rxjava3.core.Observable

class Chunk(override val observable: Observable<Any>) : ReactSocket<Any> {
    val position: Float3 = Float3(0f,0f,0f)

    fun subChunk() {

    }
}