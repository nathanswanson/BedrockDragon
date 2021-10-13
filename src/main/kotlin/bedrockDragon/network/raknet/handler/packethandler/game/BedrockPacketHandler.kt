package bedrockDragon.network.raknet.handler.packethandler.game

import bedrockDragon.network.raknet.peer.MinecraftClientPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

class BedrockPacketHandler<BedrockPacket>(sender: MinecraftClientPeer, val packet: EncapsulatedPacket, channel: Channel) {

}