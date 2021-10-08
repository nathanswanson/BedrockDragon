package bedrockDragon.network.protocol.packethandler

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel

open class EncapsulatedPacketHandler(open val sender: RakNetClientPeer, channel : Channel) : PacketHandler(channel) {
    private var orderSendIndex = Array(RakNet.CHANNEL_COUNT) {0}
    private var sequenceSendIndex = Array(RakNet.CHANNEL_COUNT) {0}
    private var messageIndex = 0
    private var splitId = 0

    fun sendNettyMessage(reliability: Reliability, channel: Int = RakNet.DEFAULT_CHANNEL.toInt(), packet: Packet) {
        val encapsulatedPacket = EncapsulatedPacket()
        encapsulatedPacket.reliability = reliability
        encapsulatedPacket.orderChannel = channel.toByte()
        encapsulatedPacket.payload = packet
        if(reliability.isReliable) {
            encapsulatedPacket.messageIndex = bumpMessageIndex()
            logger.info("Bumped message index from ${encapsulatedPacket.messageIndex} to $messageIndex")
        }
        if(reliability.isOrdered or reliability.isSequenced) {
            encapsulatedPacket.orderIndex = if (reliability.isOrdered) orderSendIndex[channel]++
            else sequenceSendIndex[channel]++

            logger.info(
                "Bumped " + (if (reliability.isOrdered) "order" else "sequence") + " index from "
                        + ((if (reliability.isOrdered) orderSendIndex[channel] else sequenceSendIndex[channel]) - 1) + " to "
                        + (if (reliability.isOrdered) orderSendIndex[channel] else sequenceSendIndex[channel]) + " on channel "
                        + channel
            )
        }

        //Split packet if needed
        if(EncapsulatedPacket.needsSplit(sender, encapsulatedPacket)) {
            encapsulatedPacket.splitId = ++splitId % 65536
            for (split in EncapsulatedPacket.split(sender, encapsulatedPacket)) {
                sender.sendQueue.add(split)
            }
            logger.info {"Split encapsulated packet and added it to the send queue" }
        } else {
            sender.sendQueue.add(encapsulatedPacket)
            logger.info {"Added encapsulated packet to the send queue" }
        }

        logger.info {
            "Sent packet with size of " + packet.size().toString() + " bytes (" + (packet.size() * 8)
                .toString() + " bits) with reliability " + reliability.toString() + " on channel " + channel
        }
    }

    private fun bumpMessageIndex(): Int {
        return messageIndex++
    }
}