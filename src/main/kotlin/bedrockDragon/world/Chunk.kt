package bedrockDragon.world

import bedrockDragon.reactive.ReactSocket
import io.reactivex.rxjava3.core.Observable

class Chunk(override val observable: Observable<Any>) : ReactSocket<Any> {
}