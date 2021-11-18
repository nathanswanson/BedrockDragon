package bedrockDragon.reactive.type

import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.player.MovePlayerPacket
import bedrockDragon.player.Player

abstract class ReactivePacket<T: PacketPayload>(val payload: T, val sender: ISubscriber) {
    var tags = emptyArray<Any>()
    var priority = 0

}

class MovePlayer(payload: MovePlayerPacket, sender: Player) : ReactivePacket<MovePlayerPacket>(payload, sender)
class AnimatePlayer()
class RotatePlayer(payload: MovePlayerPacket, sender: Player) : ReactivePacket<MovePlayerPacket>(payload, sender)
