package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class UpdateTradePacket: PacketPayload(MinecraftPacketConstants.UPDATE_TRADE) {
    var windowId: Byte = 0
    var windowType: Byte = 0
    var unknown1 = -1 //vInt
    var unknown2 = -1 //vInt
    var unknown3 = -1 //vInt
    var isWilling = false
    var tradeEntityId = -1L //sVarLong
    var playerEntityId = -1L //sVarLong
    var displayName = ""
    //todo NBT
}