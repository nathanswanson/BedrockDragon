package bedrockDragon.network.raknet.peer

import bedrockDragon.network.raknet.player.PlayerID
import bedrockDragon.network.raknet.player.Skin

class MinecraftClientPeer(val protocol: Int, val playerData: PlayerID, val skinData: Skin) {
    var status: PlayerStatus = PlayerStatus.Connected

}

enum class PlayerStatus {
    Connected,
    Authenticated,
    InGame,
}
