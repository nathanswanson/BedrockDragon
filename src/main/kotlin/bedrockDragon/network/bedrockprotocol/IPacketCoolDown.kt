package bedrockDragon.network.bedrockprotocol

interface IPacketCoolDown {
    val coolDownTime: Int

    fun canLoad() : Boolean
    fun global() : Boolean
}