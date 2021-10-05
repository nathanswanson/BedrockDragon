package bedrockDragon.network.raknet.peer

import bedrockDragon.DragonServer
import bedrockDragon.network.protocol.packethandler.logger
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_ACK
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_CUSTOM_0
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_CUSTOM_F
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_NACK
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.message.CustomPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.AcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.NotAcknowledgedPacket
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

class RakNetClientPeer(val server: DragonServer, val connectionType: ConnectionType,val clientGuid: Long,val maximumTransferUnit: Int,val channel: Channel,val sender: InetSocketAddress) {

    val KEEP_ALIVE_PING_INTERVAL = 2500

    var status: Status = Status.DISCONNECTED

    private var ackReceiptPackets = ConcurrentHashMap<EncapsulatedPacket, Int>()
    private var recieveSequenceNumber = -1
    fun update() {

    }

    /**
     * When a registered client sends a packet this function is called with that packet
     */
    fun incomingPacket(packet: RakNetPacket) {
        logger.info { packet.id }
        when(packet.id) {
            ID_NACK -> {
                val notAcknowledged = NotAcknowledgedPacket(packet)
                notAcknowledged.decode()
             }
            ID_ACK -> {
                val acknowledgedPacket = AcknowledgedPacket(packet)
                acknowledgedPacket.decode()

                for (record in acknowledgedPacket.records) {
                    var ackReceiptPacketsI = ackReceiptPackets.keys().iterator()

                    while(ackReceiptPacketsI.hasNext()) {
                        var encapsultated = ackReceiptPacketsI.next()

                    }
                }
            }
            else -> {
                if(packet.id in ID_CUSTOM_0..ID_CUSTOM_F) {
                    logger.info { "Custom Packet" }
                    val custom = CustomPacket(packet)
                    custom.decode()

                    /*
                     * We send an ACK packet as soon as we get the packet. This is
                     * because sometimes handling a packet takes longer than expected
                     * (or longer than the recovery send interval time). If this
                     * happens, it will cause the other side to resend a packet that we
                     * already got. If the resend time is too low, this can end up
                     * causing the other side to also spam us without meaning to.
                     */

                    sendAcknowledge(true, Record(custom.sequenceId))

                    /*
                    * NACK must be generated first before the peer data is updated,
                    * otherwise the data needed to know which packets have been lost
			        * will have been overwritten.
			        */

                    val skipped = custom.sequenceId - recieveSequenceNumber - 1
                    if(skipped > 0) {
                        this.sendAcknowledge(false, if (skipped == 1)
                            Record(custom.sequenceId - 1) else
                                Record(recieveSequenceNumber + 1, custom.sequenceId - 1))
                    }
                    if (custom.sequenceId > recieveSequenceNumber - 1) {
                        recieveSequenceNumber = custom.sequenceId
                        for(encapsulated in custom.messages!!) {
                            handleEncapsulatedPacket(encapsulated)
                        }
                    }
                }
            }
        }
    }

    private fun handleEncapsulatedPacket(packet: EncapsulatedPacket) {
        logger.info { packet.splitId }
    }

    private fun sendAcknowledge(acknowledge: Boolean, vararg records: Record) {

    }

    fun bumpMessageIndex(): Int {
        return 0
    }

    companion object {
        const val MAX_SPLIT_COUNT = 128
    }
}



enum class Status {
    CONNECTED, DISCONNECTED
}