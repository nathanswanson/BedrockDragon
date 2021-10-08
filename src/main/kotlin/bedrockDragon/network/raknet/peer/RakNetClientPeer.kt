package bedrockDragon.network.raknet.peer

import bedrockDragon.DragonServer
import bedrockDragon.network.protocol.PacketSortFactory
import bedrockDragon.network.protocol.packethandler.logger
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_ACK
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_CUSTOM_0
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_CUSTOM_F
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_NACK
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.message.CustomFourPacket
import bedrockDragon.network.raknet.protocol.message.CustomPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.AcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.NotAcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class RakNetClientPeer(val server: DragonServer, val connectionType: ConnectionType,val clientGuid: Long,val maximumTransferUnit: Int,val channel: Channel,val sender: InetSocketAddress) {

    private val KEEP_ALIVE_PING_INTERVAL = 2500
    private var lastAlivePing = System.currentTimeMillis()
    val sendQueue = ConcurrentLinkedQueue<EncapsulatedPacket>()
    var status: Status = Status.DISCONNECTED

    private var ackReceiptPackets = ConcurrentHashMap<EncapsulatedPacket, Int>()
    private var recieveSequenceNumber = -1

    fun update() {
        val currentTime = System.currentTimeMillis()
        //Tell client server is still alive
        //if(currentTime - lastAlivePing >= KEEP_ALIVE_PING_INTERVAL) {

        //}


        if(sendQueue.isNotEmpty()) {
            logger.info { "send packet" }
            val sendQueueI = sendQueue.iterator()
            val send = ArrayList<EncapsulatedPacket>()
            var sendLength = CustomPacket.MINIMUM_SIZE
            while (sendQueueI.hasNext()) {
                val encapsulatedPacket = sendQueueI.next()
                sendLength += encapsulatedPacket.size()
                if (sendLength > maximumTransferUnit) {
                    break
                }
                send.add(encapsulatedPacket)
                sendQueueI.remove()

                if(send.isNotEmpty()) {
                    sendCustomPacket(true, send.toTypedArray())
                }
            }
        }
    }

    /**
     * When a registered client sends a packet this function is called with that packet
     */
    fun incomingPacket(packet: RakNetPacket) {
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
        //Client has connected at this point
        val handler = PacketSortFactory.createClientPacketHandle(this, packet, channel)

        handler.responseToClient()
        handler.responseToServer()
    }

    private fun sendAcknowledge(acknowledge: Boolean, vararg records: Record) {
        val acknowledged = if (acknowledge) AcknowledgedPacket() else NotAcknowledgedPacket()
        acknowledged.records = arrayOf(*records)
        acknowledged.encode()

        sendNettyMessage(acknowledged)
        logger.info {
            "Sent " + acknowledged.records.size + " record" + (if (acknowledged.records.size == 1) "" else "s")
                .toString() + " in " + (if (acknowledged.isAcknowledgement) "ACK" else "NACK").toString() + " packet"
        }
    }

    fun bumpMessageIndex(): Int {
        return 0
    }

    companion object {
        const val MAX_SPLIT_COUNT = 128
    }

    private var sendSequenceNumber = 0


    private fun sendCustomPacket(updateRecoveryQue: Boolean, message: Array<EncapsulatedPacket>) : Int {
        val custom = CustomFourPacket()
        custom.sequenceId = sendSequenceNumber++
        custom.messages = message
        custom.encode()
        sendNettyMessage(custom)

        return custom.sequenceId
    }

    @Throws(NullPointerException::class)
    fun sendNettyMessage(buf: ByteBuf?) {

        channel.writeAndFlush(DatagramPacket(buf, sender))
    }

    @Throws(NullPointerException::class)
    fun sendNettyMessage(packet: Packet?) {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        sendNettyMessage(packet.buffer())
    }
}



enum class Status {
    CONNECTED, DISCONNECTED
}