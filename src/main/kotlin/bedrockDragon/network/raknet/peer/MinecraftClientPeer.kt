package bedrockDragon.network.raknet.peer

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

class MinecraftClientPeer(val protocol: Int, val playerData: JsonArray, val skinData: Json) {
    var status: PlayerStatus = PlayerStatus.Connected

}

enum class PlayerStatus {
    Connected,
    Authenticated,
    InGame,
}
