package bedrockDragon.network.raknet.peer

/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.CustomFourPacket
import bedrockDragon.network.raknet.protocol.message.CustomPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.AcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.NotAcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import bedrockDragon.network.raknet.handler.packethandler.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

abstract class RakNetPeer(val address: InetSocketAddress, val guid: Long, val maximumTransferUnit: Int,val connectionType: ConnectionType, val channel: Channel) {
    private var orderSendIndex = Array(RakNet.CHANNEL_COUNT) {0}
    private var sequenceSendIndex = Array(RakNet.CHANNEL_COUNT) {0}
    private var splitId = 0
    private var messageIndex = 0
    private var ackReceiptPackets = ConcurrentHashMap<EncapsulatedPacket, Int>()
    private var receiveSequenceNumber = -1
    private var sendSequenceNumber = 0
    val sendQueue = ConcurrentLinkedQueue<EncapsulatedPacket>()

    /**
     * When a registered client sends a packet this function is called with that packet
     */
    fun incomingPacket(packet: RakNetPacket) {
        when(packet.id.toInt()) {
            PacketConstants.NACK -> {
                val notAcknowledged = NotAcknowledgedPacket(packet)
                notAcknowledged.decode()
            }
            PacketConstants.ACK -> {
                val acknowledgedPacket = AcknowledgedPacket(packet)
                acknowledgedPacket.decode()

                for (record in acknowledgedPacket.records) {
                    val ackReceiptPacketsI = ackReceiptPackets.keys().iterator()

                    while(ackReceiptPacketsI.hasNext()) {
                        var encapsultated = ackReceiptPacketsI.next()
                        //TODO()
                    }
                }
            }
            else -> {
                if(packet.id in PacketConstants.CUSTOM_PACKET_RANGE) {

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

                    val skipped = custom.sequenceId - receiveSequenceNumber - 1
                    if(skipped > 0) {
                        sendAcknowledge(false, if (skipped == 1)
                            Record(custom.sequenceId - 1) else
                            Record(receiveSequenceNumber + 1, custom.sequenceId - 1)
                        )
                    }
                    if (custom.sequenceId > receiveSequenceNumber - 1) {
                        receiveSequenceNumber = custom.sequenceId
                        for(encapsulated in custom.messages!!) {
                            handleEncapsulatedPacket(encapsulated)
                        }
                    }
                }
            }
        }
    }

    @Throws(NullPointerException::class)
    fun sendNettyMessage(buf: ByteBuf?) {

        channel.writeAndFlush(DatagramPacket(buf, address))
    }

    @Throws(NullPointerException::class)
    fun sendNettyMessage(packet: Packet?) {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        sendNettyMessage(packet.buffer())
    }

    open fun handleEncapsulatedPacket(packet: EncapsulatedPacket) {
        //Client has connected at this point

    }

    private fun sendCustomPacket(updateRecoveryQue: Boolean, message: Array<EncapsulatedPacket>) : Int {
        val custom = CustomFourPacket()
        custom.sequenceId = sendSequenceNumber++
        custom.messages = message
        custom.encode()
        sendNettyMessage(custom)

        return custom.sequenceId
    }

    private fun sendAcknowledge(acknowledge: Boolean, vararg records: Record) {
        val acknowledged = if (acknowledge) AcknowledgedPacket() else NotAcknowledgedPacket()
        acknowledged.records = arrayOf(*records)
        acknowledged.encode()

        sendNettyMessage(acknowledged)
        logger.trace {
            "Sent " + acknowledged.records.size + " record" + (if (acknowledged.records.size == 1) "" else "s")
                .toString() + " in " + (if (acknowledge) "ACK" else "NACK").toString() + " packet"
        }
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        //Tell client server is still alive
        //if(currentTime - lastAlivePing >= KEEP_ALIVE_PING_INTERVAL) {

        //}


        if(sendQueue.isNotEmpty()) {
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


    fun sendMessage(reliability: Reliability, channel: Int = RakNet.DEFAULT_CHANNEL.toInt(), packet: Packet) {
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
        if(EncapsulatedPacket.needsSplit(this, encapsulatedPacket)) {
            encapsulatedPacket.splitId = ++splitId % 65536
            for (split in EncapsulatedPacket.split(this, encapsulatedPacket)) {
                sendQueue.add(split)
            }
            logger.info {"Split encapsulated packet and added it to the send queue" }
        } else {
                sendQueue.add(encapsulatedPacket)
            logger.info {"Added encapsulated packet to the send queue" }
        }

        logger.info {
            "Sent packet with size of " + packet.size().toString() + " bytes (" + (packet.size() * 8)
                .toString() + " bits) with reliability " + reliability.toString() + " on channel " + channel
        }
    }

    fun bumpMessageIndex(): Int {
        return messageIndex++
    }

    companion object {
        const val MAX_SPLIT_COUNT = 128
        const val MAX_SPLIT_PER_QUEUE = 4
        const val DETECTION_SEND_INTERVAL = 2500L
    }
}