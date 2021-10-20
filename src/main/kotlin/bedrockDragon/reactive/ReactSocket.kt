package bedrockDragon.reactive

import io.reactivex.rxjava3.core.Observable

interface ReactSocket<ReactGroup> {
    val observable: Observable<Any>
}