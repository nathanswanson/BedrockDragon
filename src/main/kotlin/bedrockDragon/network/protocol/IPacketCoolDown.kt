package bedrockDragon.network.protocol

interface IPacketCoolDown {
    val coolDownTime: Int

    fun canLoad() : Boolean
    fun global() : Boolean
}