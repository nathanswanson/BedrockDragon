package bedrockDragon.reactive.type

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class SealedBroadcaster: IBroadcaster {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val _subscription = MutableSharedFlow<SealedBroadcaster>()
    override val subscription = _subscription.asSharedFlow()

    override suspend fun invoke(emitter: IBroadcaster) {
        _subscription.emit(emitter as SealedBroadcaster)
    }

    override fun registerSubscription(watched: IBroadcaster) {
        scope.launch { watched.subscription.collectLatest { TODO() } }
    }


}