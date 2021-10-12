package bedrockDragon.network.raknet.handler

interface IPacketCoolDown {
    val coolDownTime: Int

    fun canLoad() : Boolean
    fun global() : Boolean
}