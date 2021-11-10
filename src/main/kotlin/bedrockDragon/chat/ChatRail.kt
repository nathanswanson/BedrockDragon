package bedrockDragon.chat

import bedrockDragon.player.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest


class ChatRail {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    //you have to be able to subscribe
    //and invoke
    //String, and probably filter for player
    private val _subscription = MutableSharedFlow<String>()
    val subscription = _subscription.asSharedFlow()

    fun sendMessage(message: String) {
        scope.launch { invoke(message) }
    }

    fun invoke(emitter: String) {
        scope.launch {
            _subscription.emit(emitter)
        }
    }

    fun subscribe(watcher: Player) {
        scope.launch {
            subscription.collectLatest { message ->
                watcher.sendMessage(message)
            }
        }

    }

    companion object {
        val DEFAULT = ChatRail()
    }
}