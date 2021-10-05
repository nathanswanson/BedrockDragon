package bedrockDragon.network.raknet.protocol.message

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import kotlin.experimental.and

@OptIn(ExperimentalUnsignedTypes::class)
class EncapsulatedPacket : Cloneable {

    private var isClone = false
    private var clone: EncapsulatedPacket? = null
    var reliability: Reliability = TODO()
    var ackRecord: Record = TODO()
    var payload: Packet = TODO()
    var split = false

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
        reliability = Reliability.lookup((flags.toInt() and FLAG_RELIABILITY) shl FLAG_RELIABILITY_INDEX)!!//TODO

        split = flags and FLAG_SPLIT.toShort() > 0
        val length = buffer.readUnsignedShort() / Byte.SIZE_BITS.toUInt()
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

        const val FLAG_RELIABILITY_INDEX = 5
        const val FLAG_RELIABILITY = 0b11100000
        const val FLAG_SPLIT = 0b00010000
        const val MINIMUM_SIZE = 3
    }
}