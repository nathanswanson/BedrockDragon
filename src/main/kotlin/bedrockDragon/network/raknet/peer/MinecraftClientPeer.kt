package bedrockDragon.network.raknet.peer

import com.philjay.jwt.JWT
import kotlinx.serialization.json.JsonArray

class MinecraftClientPeer(val protocol: Int, val playerData: JsonArray, val skinData: JWT) {
    var status: PlayerStatus = PlayerStatus.Connected

}

enum class PlayerStatus {
    Connected,
    Authenticated,
    InGame,
}
