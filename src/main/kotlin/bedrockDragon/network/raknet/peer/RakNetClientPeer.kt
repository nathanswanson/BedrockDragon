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

import bedrockDragon.DragonServer
import bedrockDragon.network.raknet.protocol.packet.PacketSortFactory
import bedrockDragon.network.raknet.protocol.packet.packethandler.logger
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.message.CustomPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.AcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.NotAcknowledgedPacket
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import io.netty.channel.Channel
import java.net.InetSocketAddress

class RakNetClientPeer(val server: DragonServer, connectionType: ConnectionType, guid: Long, maximumTransferUnit: Int, channel: Channel, val sender: InetSocketAddress)
    : RakNetPeer(sender, guid, maximumTransferUnit, connectionType, channel){

    private var lastAlivePing = System.currentTimeMillis()
    var status: Status = Status.DISCONNECTED


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



}



enum class Status {
    CONNECTED, DISCONNECTED
}