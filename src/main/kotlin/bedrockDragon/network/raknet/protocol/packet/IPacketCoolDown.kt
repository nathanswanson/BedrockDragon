package bedrockDragon.network.raknet.protocol.packet

interface IPacketCoolDown {
    val coolDownTime: Int

    fun canLoad() : Boolean
    fun global() : Boolean
}