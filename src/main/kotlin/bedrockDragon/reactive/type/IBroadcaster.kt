package bedrockDragon.reactive.type

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface IBroadcaster {

    val subscription: SharedFlow<IBroadcaster>
    suspend fun invoke(emitter: IBroadcaster)
    fun registerSubscription(watched: IBroadcaster)
}