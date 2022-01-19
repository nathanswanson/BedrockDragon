package bedrockDragon.network.raknet.protocol.game.item

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class BookEditPacket: PacketPayload(MinecraftPacketConstants.BOOK_EDIT) {

    lateinit var usage: BookFunction
    var inventorySlot = -1
    var pageNumber = -1
    var secondaryPageNumber = -1
    var text: String = ""
    var photo: String = ""

    var title: String = ""
    var author: String = ""
    var xuid: String = ""

    override fun decode(packet: Packet) {
        usage = BookFunction.values()[packet.readByte().toInt()]
        inventorySlot = packet.readByte().toInt()
        when(usage) {
            BookFunction.REPLACE,
            BookFunction.ADD -> {
                pageNumber = packet.readByte().toInt()
                text = packet.readString()
                photo = packet.readString()
            }
            BookFunction.DELETE -> {
                pageNumber = packet.readByte().toInt()
            }
            BookFunction.SWAP -> {
                pageNumber = packet.readByte().toInt()
                secondaryPageNumber = packet.readByte().toInt()
            }
            BookFunction.SIGN -> {
                title = packet.readString()
                author = packet.readString()
                xuid = packet.readString()
            }
        }
    }


    enum class BookFunction {
        REPLACE,
        ADD,
        DELETE,
        SWAP,
        SIGN,
    }
}