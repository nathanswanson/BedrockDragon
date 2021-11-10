package bedrockDragon.reactive.type

import bedrockDragon.reactive.Reactor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChunkRelay {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val _subscription = MutableSharedFlow<Reactor>()
    val subscription = _subscription.asSharedFlow()

    fun invoke(emitter: Reactor) {
        scope.launch {
            _subscription.emit(emitter)
        }
    }

    fun subscribe(watcher: SubscribedEventHandler) {
        scope.launch {
            subscription.collectLatest {
                watcher.invoke(it)
            }
        }
    }
}