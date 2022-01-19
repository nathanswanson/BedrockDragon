package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload

class PlayStatusPacket(var status: Int): PacketPayload(MinecraftPacketConstants.PLAY_STATUS) {
/*
0	Login success	Sent after Login has been successfully decoded and the player has logged in
1	Failed client	Displays "Could not connect: Outdated client!"
2	Failed server	Displays "Could not connect: Outdated server!"
3	Player spawn	Sent after world data to spawn the player
4	Failed invalid Tenant	Displays "Unable to connect to world. Your school does not have access to this server."
5	Failed Vanilla Edu.	Displays "The server is not running Minecraft: Education Edition. Failed to connect."
6	Failed Edu. Vanilla	Displays "The server is running an incompatible edition of Minecraft. Failed to connect."
7	Failed server full 	Displays "Wow this server is popular! Check back later to see if space opens up. Server Full"
 */
override fun encode() {
        writeInt(status)
    }

    override fun decode(packet: Packet) {
        status = packet.buffer().readInt()
    }
}