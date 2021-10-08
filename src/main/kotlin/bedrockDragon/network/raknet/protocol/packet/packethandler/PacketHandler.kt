package bedrockDragon.network.raknet.protocol.packet.packethandler

import bedrockDragon.network.raknet.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import mu.KotlinLogging
import java.net.InetSocketAddress

val logger = KotlinLogging.logger {}

abstract class PacketHandler(val channel : Channel) {

    var finished: Boolean = false
    open fun responseToClient() {}
    open fun responseToServer() {}

    /**
     * Sends a Netty message over the channel raw.
     *
     *
     * This should be used sparingly, as if it is used incorrectly it could
     * break client peers entirely. In order to send a message to a peer, use
     * one of the
     * [ sendMessage()][bedrockDragon.network.raknet.peer.RakNetPeer.sendMessage] methods.
     *
     * @param packet
     * the packet to send.
     * @param address
     * the address to send the packet to.
     * @throws NullPointerException
     * if the `packet`, `address`, or IP
     * address of the `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun sendNettyMessage(packet: Packet?, address: InetSocketAddress?) {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        sendNettyMessage(packet.buffer(), address)
    }

    /**
     * Sends a Netty message over the channel raw.
     *
     *
     * This should be used sparingly, as if it is used incorrectly it could
     * break client peers entirely. In order to send a message to a peer, use
     * one of the
     * [ sendMessage()][bedrockDragon.network.raknet.peer.RakNetPeer.sendMessage] methods.
     *
     * @param packet
     * the packet to send.
     * @param address
     * the address to send the packet to.
     * @throws NullPointerException
     * if the `packet`, `address`, or IP
     * address of the `address` are `null`.
     */
    @Throws(NullPointerException::class)
    fun sendNettyMessage(buf: ByteBuf?, address: InetSocketAddress?) {
        if (buf == null) {
            throw NullPointerException("Buffer cannot be null")
        } else if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        channel.writeAndFlush(DatagramPacket(buf, address))
        logger.trace("Sent netty message with size of " + buf.capacity() + " bytes (" + (buf.capacity() * 8)
        		+ " bits) to " + address);
    }

}

