package bedrockDragon.network.raknet.identifier

import bedrockDragon.network.raknet.Packet

class MinecraftServerMessage(MOTDMessage: String): Packet() {
    //TODO finish motd live data
    private val MOTD: MinecraftMOTD = MinecraftMOTD(
        protocolVer = 0,
        playerCount = 0,
        minecraftVer = "",
        guid = 0,
        maxPlayerCount = 0
    )

    //to protect the data in this class everytime the MOTD needs to be updated
    //refresh must be called.

    fun refresh() {

    }

    fun changeMessage() {

    }

    //this is what the server calls to figure out what the current MOTD is
    fun MOTDBinary(): ByteArray {
        return TODO()
    }

    private data class MinecraftMOTD(
        val edition: String = "MCPE",
        val motd1: String = "Bedrock Dragon Server",
        val protocolVer: Int, //Dynamic
        val minecraftVer: String, //Dynamic
        val playerCount: Int, //Dynamic
        val maxPlayerCount: Int,
        val guid: Int, //Dynamic
        val motd2: String = "by Nathan Swanson",
        val gameMode: String = "Survival",
        val gameModeVal: Int = 0,
        val portIv4: Int = 19132,
        val portIv6: Int = 19133
    ) {
        override fun toString(): String {
            return "$edition;$motd1;$protocolVer;$minecraftVer;$playerCount;" +
                    "$maxPlayerCount;$guid;$motd2;$gameMode;$gameModeVal;$portIv4;$portIv6"
        }
    }

}


