package bedrockDragon.network.raknet.identifier

import bedrockDragon.MINECRAFT_VERSION
import bedrockDragon.PROTOCOL_VERSION
import bedrockDragon.network.raknet.Packet
import bedrockDragon.resource.ServerProperties

class MinecraftServerMessage(MOTDMessage: String): Packet() {
    val motd: MinecraftMOTD = MinecraftMOTD()

    //to protect the data in this class everytime the MOTD needs to be updated
    //refresh must be called.

    fun refresh() {

    }

    data class MinecraftMOTD(
        val edition: String = "MCPE",
        val motd1: String = ServerProperties.getProperty("motd"),
        val protocolVer: String = PROTOCOL_VERSION, //Dynamic
        val minecraftVer: String = MINECRAFT_VERSION, //Dynamic
        val playerCount: Int = 0, //Dynamic
        val maxPlayerCount: Int = Integer.parseInt(ServerProperties.getProperty("max-players")),
        val guid: Int = 0, //Dynamic
        val motd2: String = ServerProperties.getProperty("motd2"),
        val gameMode: String = ServerProperties.getProperty("gamemode"),
        val gameModeVal: Int = if(gameMode == "Survival") 0 else 1,
        val portIv4: Int = Integer.parseInt(ServerProperties.getProperty("server-port")),
        val portIv6: Int = 19133
    ) {
        override fun toString(): String {
            return "$edition;$motd1;$protocolVer;$minecraftVer;$playerCount;" +
                    "$maxPlayerCount;$guid;$motd2;$gameMode;$gameModeVal;$portIv4;$portIv6"
        }
    }

}


