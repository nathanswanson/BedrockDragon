package bedrockDragon.network.raknet.protocol.game.inventory

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import java.util.*

class InventoryTransactionPacket: PacketPayload(MinecraftPacketConstants.INVENTORY_TRANSACTION) {
    //attribution: Nukkit had this list in java
    val TYPE_NORMAL = 0
    val TYPE_MISMATCH = 1
    val TYPE_USE_ITEM = 2
    val TYPE_USE_ITEM_ON_ENTITY = 3
    val TYPE_RELEASE_ITEM = 4

    val USE_ITEM_ACTION_CLICK_BLOCK = 0
    val USE_ITEM_ACTION_CLICK_AIR = 1
    val USE_ITEM_ACTION_BREAK_BLOCK = 2

    val RELEASE_ITEM_ACTION_RELEASE = 0 //bow shoot

    val RELEASE_ITEM_ACTION_CONSUME = 1 //eat food, drink potion


    val USE_ITEM_ON_ENTITY_ACTION_INTERACT = 0
    val USE_ITEM_ON_ENTITY_ACTION_ATTACK = 1


    val ACTION_MAGIC_SLOT_DROP_ITEM = 0
    val ACTION_MAGIC_SLOT_PICKUP_ITEM = 1

    val ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM = 0
    val ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM = 1

    var transactionType = -1
    //todo InvAction
    //todo TransactionData
    var legacyRequestId = -1


}