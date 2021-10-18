package bedrockDragon.network.raknet.peer

import kotlinx.serialization.json.*

class MinecraftClientPeer(val protocol: Int, val playerData: JsonObject, val skinData: String): MinecraftPeer() {
    //TODO() class for playerData
    var status: PlayerStatus = PlayerStatus.Connected
    var uuid: String
    var xuid: Long
    var userName: String
    init {
        val jsonLinkedHashMap = playerData.values.iterator()
        xuid = (jsonLinkedHashMap.next() as JsonPrimitive).long
        uuid = (jsonLinkedHashMap.next() as JsonPrimitive).content
        userName = (jsonLinkedHashMap.next() as JsonPrimitive).content

    }
}

enum class PlayerStatus {
    Connected,
    Authenticated,
    InGame,
}
