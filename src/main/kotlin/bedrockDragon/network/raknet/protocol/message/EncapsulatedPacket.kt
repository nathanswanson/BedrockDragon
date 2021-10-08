package bedrockDragon.network.raknet.protocol.message

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import java.util.*
import kotlin.experimental.and
import kotlin.math.ceil


@OptIn(ExperimentalUnsignedTypes::class)
class EncapsulatedPacket : Cloneable {

    private var isClone = false
    private var clone: EncapsulatedPacket? = null
    lateinit var reliability: Reliability
    lateinit var ackRecord: Record
    lateinit var payload: Packet
    var split = false

    /**
     * The amount of packets the original packet is split up into.
     */
    var splitCount = 0

    /**
     * The ID the split packet.
     */
    var splitId = 0

    /**
     * The index of this split part of the packet in the overall packet. This is
     * used to determine the order in which to put the split packet back
     * together when each and every part has been received.
     */
    var splitIndex = 0

    /**
     * The message index. This is only ever used if the reliability is of the
     * [RELIABLE][Reliability.RELIABLE] type. This should always be one
     * higher than the message index of the last reliable packet that was sent,
     * as it is used to let the receiver know whether or not they have missed a
     * reliable packet in transmission. This crucial in order for the
     * [RELIABLE_ORDERED][Reliability.RELIABLE_ORDERED] reliability to
     * function.
     *
     *
     * It works like this:
     *
     *  * 1. Sender sends reliable packet one to receiver.
     *  * 2. Receiver receives reliable packet one.
     *  * 3. Sender sends reliable packet two to receiver.
     *  * 4. Receiver does not receive reliable packet two as it is lost in
     * transmission.
     *  * 5. Sender sends reliable packet three to receiver.
     *  * 6. Receiver receives reliable packet three, and realizes that it has
     * lost reliable packet two, causing it to send a
     * [ NACK][com.whirvis.jraknet.protocol.message.acknowledge.NotAcknowledgedPacket] packet.
     *  * 7. Sender sends reliable packet two again.
     *  * 8. Receiver receives reliable packet two and communication continues
     * as normal.
     *
     *
     *
     * When a receiver receives a reliable packet, it is also always supposed to
     * send an
     * [ ACK][com.whirvis.jraknet.protocol.message.acknowledge.AcknowledgedPacket] packet back to the sender. This lets the sender know that the packet
     * has been received so it can be removed from its cache. It is assumed that
     * the receiver sent an
     * [ ACK][com.whirvis.jraknet.protocol.message.acknowledge.AcknowledgedPacket] packet in each step describing the fact that it received a reliable
     * packet.
     */
    var messageIndex = 0

    /**
     * The order index. This is only ever used if the reliability if of the
     * [ORDERED][Reliability.RELIABLE_ORDERED] or
     * [SEQUENCED][Reliability.UNRELIABLE_SEQUENCED] type.
     *
     *
     * This is similar to the `messageIndex`, however this is used to
     * determine when ordered packets are ordered and if a sequenced packet is
     * the newest one or not. On the other hand, `messageIndex` is
     * used to determine when reliable packets have been lost in transmission.
     */
    var orderIndex = 0

    /**
     * The order index. This is only ever used if the reliability if of the
     * [ORDERED][Reliability.RELIABLE_ORDERED] or
     * [SEQUENCED][Reliability.UNRELIABLE_SEQUENCED] type.
     *
     *
     * In total, there are a total of
     * {@value com.whirvis.jraknet.RakNet#CHANNEL_COUNT} channels that can be
     * used to send [ORDERED][Reliability.RELIABLE_ORDERED] and
     * [SEQUENCED][Reliability.UNRELIABLE_SEQUENCED] packets on. Both have
     * their own set of these channels. It is good to make use of this if there
     * are many different operations that must be ordered or sequenced happening
     * at the same time, as it can help prevent clogging.
     */
    var orderChannel: Byte = 0

    @Throws (RuntimeException::class)
    override fun clone(): EncapsulatedPacket {
        if(isClone) {
            throw CloneNotSupportedException("Encapsulated packets can only be cloned once")
        } else if (clone == null) {
            try {
                clone = super.clone() as EncapsulatedPacket
                clone!!.isClone = true
            } catch (e : CloneNotSupportedException) {
                throw RuntimeException(e)
            }
        }
        return this.clone!!
    }

    /**
     * Encodes the packet.
     *
     * @param buffer
     *            the buffer to write to.
     * @throws NullPointerException
     *             if the <code>reliability</code>, <code>payload</code>, or
     *             <code>buffer</code> are <code>null</code>, or if the
     *             reliability is reliable and the <code>ackRecord</code> is
     *             <code>null</code>.
     * @throws IllegalArgumentException
     *             if the <code>ackRecord</code> is ranged.
     */

    fun encode(buffer: Packet) {
        var flags = 0x00
        flags = flags or (reliability.id.toInt() shl FLAG_RELIABILITY_INDEX)
        flags = flags or if (split) FLAG_SPLIT else 0
        buffer.writeByte(flags)
        buffer.writeUnsignedShort(payload.size() * Byte.SIZE_BITS)

        //if(ackRecord.isRanged) {
        //    throw IllegalArgumentException("ACK record cannot be ranged")
        //}

        if(reliability.isReliable) {
            buffer.writeTriadLE(messageIndex)
        }
        if(reliability.isOrdered || reliability.isSequenced) {
            buffer.writeTriadLE(orderIndex)
            buffer.writeUnsignedShort(orderChannel.toInt())
        }
        if(split) {
            buffer.writeInt(splitCount)
            buffer.writeUnsignedShort(splitId)
            buffer.writeInt(splitIndex)
        }
        payload.array()?.let { buffer.writeB(*it) }
    }

    /**
     * Calculates the size of the packet if it had been encoded.
     *
     * @return the size of the packet if it had been encoded.
     */

    fun size() : Int {
        return size(reliability, split, payload)
    }

    /**
     * Decodes the packet.
     *
     * @param buffer
     *            the buffer to read from.
     * @throws NullPointerException
     *             if the <code>buffer</code> is <code>null</code>, or if the
     *             <code>reliability</code> failed to lookup (normally due to an
     *             invalid ID).
     */

    fun decode(buffer: Packet) {
        val flags: Short = buffer.readUnsignedByte()
        reliability = Reliability.lookup((flags.toInt() and FLAG_RELIABILITY) shr FLAG_RELIABILITY_INDEX)!!

        split = flags and FLAG_SPLIT.toShort() > 0
        val length = buffer.readUnsignedShort() / Byte.SIZE_BITS.toUInt()
        if(reliability.isReliable) {
            messageIndex = buffer.readTriadLE()
        }
        if (reliability.isOrdered or reliability.isSequenced) {
            orderIndex = buffer.readTriadLE()
            orderChannel = buffer.readByte()
        }
        if (split) {
            splitCount = buffer.readInt()
            splitId = buffer.readUnsignedShort().toInt()
            splitIndex = buffer.readInt()
        }
        payload = Packet(buffer.read(length.toInt()))
    }

    companion object {

        /**
         * Calculates the size of an encapsulated packet if it had been encoded.
         *
         * @param reliability
         *            the reliability.
         * @param split
         *            <code>true</code> if the packet is split, <code>false</code>
         *            otherwise.
         * @return the size.
         */
        fun size(reliability: Reliability, split: Boolean): Int {
            return size(reliability, split, null)
        }

        fun size(reliability: Reliability, split: Boolean, payload: Packet?): Int {
            var size = 3
            size += if (reliability.isReliable) 3 else 0
            size += if(reliability.isOrdered || reliability.isSequenced) 4 else 0

            size += if (split) 10 else 0
            size += payload?.size() ?: 0
            return size
        }

        fun needsSplit(peer: RakNetClientPeer, encapsulatedPacket: EncapsulatedPacket) : Boolean {
            return CustomPacket.MINIMUM_SIZE + encapsulatedPacket.size() > peer.maximumTransferUnit
        }

        fun split(peer: RakNetClientPeer, encapsulatedPacket: EncapsulatedPacket): Array<EncapsulatedPacket> {
            val size = peer.maximumTransferUnit -
                    CustomPacket.MINIMUM_SIZE -
                    size(encapsulatedPacket.reliability, true)
            val src = encapsulatedPacket.payload.array()!!
            var payloadIndex = 0
            var splitIndex = 0
            val split = Array(ceil(src.size.toDouble() / size.toDouble()).toInt()) { ByteArray(size) }

            while(payloadIndex < src.size) {
                if(payloadIndex + size <= src.size) {
                    split[splitIndex++] = src.copyOfRange(payloadIndex, payloadIndex + size)
                    payloadIndex += size
                } else {
                    split[splitIndex++] = src.copyOfRange(payloadIndex, payloadIndex + src.size)
                    payloadIndex = src.size
                }
            }

            val splitPackets = Array(split.size) {EncapsulatedPacket()}

            for(i in splitPackets.indices) {
                splitPackets[i].reliability = encapsulatedPacket.reliability
                splitPackets[i].payload = Packet(split[i])
                splitPackets[i].messageIndex = if (encapsulatedPacket.reliability.isReliable) peer.bumpMessageIndex() else 0
                if(encapsulatedPacket.reliability.isOrdered or encapsulatedPacket.reliability.isSequenced) {
                    splitPackets[i].orderChannel = encapsulatedPacket.orderChannel
                    splitPackets[i].orderIndex = encapsulatedPacket.orderIndex
                }
                splitPackets[i].split = true
                splitPackets[i].splitCount = split.size
                splitPackets[i].splitId = encapsulatedPacket.splitId
                splitPackets[i].splitIndex = i
            }
            return splitPackets
        }

        const val FLAG_RELIABILITY_INDEX = 5
        const val FLAG_RELIABILITY = 0b11100000
        const val FLAG_SPLIT = 0b00010000
        const val MINIMUM_SIZE = 3
    }
}