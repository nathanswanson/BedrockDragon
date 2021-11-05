package bedrockDragon.chat

import bedrockDragon.player.Player
import bedrockDragon.reactive.type.SealedBroadcaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ChatRail: SealedBroadcaster() {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun sendMessage(message: String) {
        scope.launch { invoke(message) }
    }

    companion object {
        val DEFAULT = ChatRail()
    }
}