/*
 *    __     ______     ______     __  __     __   __     ______     ______
 *   /\ \   /\  == \   /\  __ \   /\ \/ /    /\ "-.\ \   /\  ___\   /\__  _\
 *  _\_\ \  \ \  __<   \ \  __ \  \ \  _"-.  \ \ \-.  \  \ \  __\   \/_/\ \/
 * /\_____\  \ \_\ \_\  \ \_\ \_\  \ \_\ \_\  \ \_\\"\_\  \ \_____\    \ \_\
 * \/_____/   \/_/ /_/   \/_/\/_/   \/_/\/_/   \/_/ \/_/   \/_____/     \/_/
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2020 "Whirvis" Trent Summerlin
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
package bedrockDragon.debug.clientSimulator

import bedrockDragon.network.raknet.protocol.packet.packethandler.logger
import bedrockDragon.network.raknet.InvalidChannelException
import bedrockDragon.network.raknet.RakNet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_CONNECTION_REQUEST_ACCEPTED
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_DISCONNECTION_NOTIFICATION
import bedrockDragon.network.raknet.RakNetPacket.Companion.ID_USER_PACKET_ENUM
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.peer.Status
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted
import bedrockDragon.network.raknet.protocol.login.NewIncomingConnection
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A server connection that handles login and other server related protocols.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class RakNetServerPeer(
    private val client: RakNetClient,address: InetSocketAddress, guid: Long,maximumTransferUnit: Int,
    connectionType: ConnectionType, channel: Channel
) : RakNetPeer(address, guid, maximumTransferUnit, connectionType, channel){
    private var loginRecord: EncapsulatedPacket? = null
    private var timestamp: Long = 0
    private var messageIndex = 0
    var status : Status = Status.DISCONNECTED

    fun getTimestamp(): Long {
        return System.currentTimeMillis() - timestamp
    }

    fun handleMessage(packet: RakNetPacket, channel: Int) {
        if (packet.id == ID_CONNECTION_REQUEST_ACCEPTED) {
            val connectionRequestAccepted = ConnectionRequestAccepted(packet)
            connectionRequestAccepted.decode()
            if (!connectionRequestAccepted.failed()) {
                val newIncomingConnection = NewIncomingConnection()
                newIncomingConnection.serverAddress = this.address
                newIncomingConnection.clientTimestamp = connectionRequestAccepted.clientTimestamp
                newIncomingConnection.serverTimestamp = connectionRequestAccepted.serverTimestamp
                newIncomingConnection.encode()
                if (!newIncomingConnection.failed()) {
                    loginRecord = sendMessage(
                        Reliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT,
                        newIncomingConnection
                    )
                } else {
                    client.disconnect(
                        "Failed to login (" + newIncomingConnection.javaClass.simpleName
                                + " failed to encode)"
                    )
                }
            } else {
                client.disconnect(
                    ("Failed to login (" + connectionRequestAccepted.javaClass.simpleName
                            + " failed to decode)")
                )
            }
        } else if (packet.id == ID_DISCONNECTION_NOTIFICATION) {
            client.disconnect("Server disconnected")
        } else if (packet.id >= ID_USER_PACKET_ENUM) {
            client.callEvent { listener: RakNetClientListener ->
                listener.handleMessage(
                    client, this, packet, channel
                )
            }
        } else {
            client.callEvent { listener: RakNetClientListener ->
                listener.handleUnknownMessage(
                    client, this, packet, channel
                )
            }
        }
    }

    fun sendMessage(reliability: Reliability, packet: RakNetPacket): EncapsulatedPacket? {
        return sendMessage(reliability, 0, packet)
    }

    fun sendMessage(reliability: Reliability, channel: Int, packet: RakNetPacket): EncapsulatedPacket {

            if (channel >= RakNet.CHANNEL_COUNT) {
                throw InvalidChannelException(channel);
            }

            // Generate encapsulated packet
            var encapsulated = EncapsulatedPacket();
            encapsulated.reliability = reliability;
            encapsulated.orderChannel = channel.toByte();
            encapsulated.payload = packet;
            if (reliability.isReliable) {
                encapsulated.messageIndex = this.bumpMessageIndex()
            }

            if (reliability.isOrdered or reliability.isSequenced) {
                encapsulated.orderIndex = if (reliability.isOrdered) orderSendIndex[channel]++ else
                sequenceSendIndex[channel]++;
            }

            // Add to send queue
            if (EncapsulatedPacket.needsSplit(this, encapsulated)) {
                encapsulated.splitId = ++splitId % 65536;
                for (split in EncapsulatedPacket.split(this, encapsulated)) {
                    sendQueue.add(split);
                }
            } else {
                sendQueue.add(encapsulated);
            }


            /*
             * Return a copy of the encapsulated packet as if a single variable is
             * modified in the encapsulated packet before it is sent, the
             * communication with the peer could cease to function entirely.
             */

            return encapsulated
    }

    fun onAcknowledge(record: Record, packet: EncapsulatedPacket?) {
        if ((record == loginRecord!!.ackRecord)) {
            timestamp = System.currentTimeMillis()

            client.callEvent { listener: RakNetClientListener ->
                listener.onLogin(
                    client, this
                )
            }
        }
        client.callEvent { listener: RakNetClientListener ->
            listener.onAcknowledge(
                client, this, record, packet
            )
        }
    }

    fun onNotAcknowledge(record: Record?, packet: EncapsulatedPacket?) {
        client.callEvent { listener: RakNetClientListener ->
            listener.onLoss(
                client, this, record, packet
            )
        }
    }

    fun update() {
        logger.info { "Connected" }
    }

    fun disconnect() {

    }

    fun handleInternal(packet: RakNetPacket) {

    }


}