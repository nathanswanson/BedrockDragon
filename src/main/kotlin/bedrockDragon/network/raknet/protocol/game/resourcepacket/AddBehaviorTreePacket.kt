package bedrockDragon.network.raknet.protocol.game.resourcepacket

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class AddBehaviorTreePacket: PacketPayload(MinecraftPacketConstants.ADD_BEHAVIOR_TREE) {
    var tree = ""

    override fun encode() {
        writeString(tree)
    }
}