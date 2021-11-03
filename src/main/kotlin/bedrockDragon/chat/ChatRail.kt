package bedrockDragon.chat

import bedrockDragon.player.Player
import bedrockDragon.reactive.type.Broadcaster

class ChatRail: Broadcaster<Player>() {

    companion object {
        var chatRail = ChatRail()

        fun DEFAULT(): ChatRail {
            return chatRail
        }
    }
}