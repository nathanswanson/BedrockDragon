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
package bedrockDragon.network.raknet

import bedrockDragon.network.raknet.protocol.ConnectionType
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import java.util.*

/**
 * A generic RakNet packet that has the ability to get the ID of the packet
 * along with encoding and decoding.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
open class RakNetPacket : Packet {
    /**
     * Returns the ID of the packet.
     *
     * @return the ID of the packet.
     */

	var id: Short = 0
        protected set

    private val supportsEncoding: Boolean
    private val supportsDecoding: Boolean

    /**
     * Creates a RakNet packet.
     *
     * @param id
     * the ID of the packet.
     * @throws IllegalArgumentException
     * if the `id` is not in between `0-255`.
     */
    constructor(id: Int) : super() {
        require(!(id < 0x00 || id > 0xFF)) { "ID must be in between 0-255" }
        writeUnsignedByte(id)
        supportsEncoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, ENCODE_METHOD_NAME)
        supportsDecoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, DECODE_METHOD_NAME)
    }

    /**
     * Creates a RakNet packet.
     *
     * @param buffer
     * the buffer to read from and write to. The buffer must have at
     * least one byte to be read from for the ID.
     * @throws IllegalArgumentException
     * if the `buffer` has less than `1`
     * readable `byte`.
     */
    constructor(buffer: ByteBuf?) : super(buffer) {
        require(remaining() >= 1) { "Buffer must have at least one readable byte for the ID" }
        id = readUnsignedByte()
        supportsEncoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, ENCODE_METHOD_NAME)
        supportsDecoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, DECODE_METHOD_NAME)
    }

    /**
     * Creates a RakNet packet.
     *
     * @param datagram
     * the datagram packet to read from. The datagram must have at
     * least one byte to be read from for the ID.
     * @throws IllegalArgumentException
     * if the buffer contained within the datagram has less than
     * `1` readable `byte`.
     * @see .RakNetPacket
     */
    constructor(datagram: DatagramPacket) : this(datagram.content()) {}

    /**
     * Creates a RakNet packet.
     *
     * @param data
     * the byte array to read to read from. The byte array must have
     * at least one byte to be read from for the ID.
     * @throws IllegalArgumentException
     * if the length of the `data` is less than
     * `1`.
     * @see .RakNetPacket
     */
    constructor(data: ByteArray?) : this(Unpooled.copiedBuffer(data)) {}

    /**
     * Creates a RakNet packet.
     *
     * @param packet
     * the packet to read from and write to. The packet must have at
     * least one byte to be read from for the ID. If the packet is an
     * instance of [RakNetPacket], it will be casted and have
     * its ID retrieved via [.getId].
     * @throws IllegalArgumentException
     * if the packet size has less than `1` readable
     * `byte` and is not an instance of
     * [RakNetPacket].
     */
    constructor(packet: Packet) : super(packet) {
        if (packet is RakNetPacket) {
            id = packet.id
        } else {
            require(remaining() >= 1) { "The packet must have at least one byte to read the ID" }
            id = readUnsignedByte()
        }
        supportsEncoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, ENCODE_METHOD_NAME)
        supportsDecoding = isMethodOverriden(this.javaClass, RakNetPacket::class.java, DECODE_METHOD_NAME)
    }

    /**
     * Reads a magic array and returns whether or not it is valid.
     *
     * @return `true` if the magic array was valid,
     * `false` otherwise.
     */
    fun readMagic(): Boolean {
        val magicCheck = this.read(MAGIC.size)
        return Arrays.equals(MAGIC, magicCheck)
    }

    /**
     * Reads a [ConnectionType].
     *
     *
     * This method will check to make sure if there is at least enough data to
     * read the the connection type magic before reading the data. This is due
     * to the fact that this is meant to be used strictly at the end of packets
     * that can be used to signify the protocol implementation of the sender.
     *
     * @return a [ConnectionType], [ConnectionType.VANILLA] if not
     * enough data to read one is present.
     * @throws RakNetException
     * if not enough data is present in the packet after the
     * connection type magic or there are duplicate keys in the
     * metadata.
     */
    @Throws(RakNetException::class)
    fun readConnectionType(): ConnectionType {
        if (remaining() >= ConnectionType.MAGIC.size) {
            val connectionMagicCheck = this.read(ConnectionType.MAGIC.size)
            if (Arrays.equals(ConnectionType.MAGIC, connectionMagicCheck)) {
                val uuid = readUUID()
                val name = readString()
                val language = readString()
                val version = readString()
                val metadata = HashMap<String, String>()
                val metadataLength = readUnsignedByte().toInt()
                //for (i in 0) {
                //val key = readString()
                //    val value = readString()
                //    if (metadata.containsKey(key)) {
                //        throw RakNetException("Duplicate metadata key \"$key\"")
                 //   }
                //    metadata[key] = value
                //}
                return ConnectionType(uuid, name, language, version, metadata)
            }
        }
        return ConnectionType.VANILLA
    }

    /**
     * Writes the magic sequence to the packet.
     *
     * @return the packet.
     */
    fun writeMagic(): RakNetPacket {
        this.write(*MAGIC)
        return this
    }

    /**
     * Writes a [ConnectionType] to the packet.
     *
     * @param connectionType
     * the connection type, a `null` value will have
     * [ConnectionType.JRAKNET] connection type be used
     * instead.
     * @return the packet.
     * @throws RakNetException
     * if there are too many values in the metadata.
     */
    @Throws(RakNetException::class)
    fun writeConnectionType(connectionType: ConnectionType?): Packet {
        var connectionType = connectionType
        connectionType = connectionType ?: ConnectionType.JRAKNET
        this.write(*ConnectionType.MAGIC)
        writeUUID(connectionType.uuid)
        writeString(connectionType.name)
        writeString(connectionType.language)
        writeString(connectionType.version)
        if (connectionType.metaData.size > ConnectionType.MAX_METADATA_VALUES) {
            throw RakNetException("Too many metadata values")
        }
        writeUnsignedByte(connectionType.metaData.size)
        for ((key, value) in connectionType.metaData) {
            writeString(key)
            writeString(value)
        }
        return this
    }

    /**
     * Writes the [ConnectionType.JRAKNET] connection type to the packet.
     *
     * @return the packet.
     * @throws RuntimeException
     * if a `RakNetException` is caught despite the fact
     * that this method should never throw an error in the first
     * place.
     */
    @Throws(RuntimeException::class)
    fun writeConnectionType(): Packet {
        return try {
            this.writeConnectionType(null)
        } catch (e: RakNetException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Returns whether or not encoding is supported. If encoding is not
     * supported, calling [.encode] will yield an
     * `UnsupportedOperationException`.
     *
     * @return `true` if encoding is supported, `false`
     * otherwise.
     */
    fun supportsEncoding(): Boolean {
        return supportsEncoding
    }

    /**
     * Encodes the packet.
     *
     * @throws UnsupportedOperationException
     * if encoding the packet is not supported.
     */
    @Throws(UnsupportedOperationException::class)
    open fun encode() {
        throw UnsupportedOperationException("Encoding not supported")
    }

    /**
     * Returns whether or not decoding is supported. If decoding is not
     * supported, calling [.decode] will yield an
     * `UnsupportedOperationException`.
     *
     * @return `true` if decoding is supported, `false`
     * otherwise.
     */
    fun supportsDecoding(): Boolean {
        return supportsDecoding
    }

    /**
     * Decodes the packet.
     *
     * @throws UnsupportedOperationException
     * if decoding the packet is not supported.
     */
    @Throws(UnsupportedOperationException::class)
    open fun decode() {
        throw UnsupportedOperationException("Decoding not supported")
    }

    /**
     * Updates the buffer.
     *
     * @param buffer
     * the buffer to read from and write to, a `null`
     * value will have a new buffer be used instead.
     * @param updateId
     * `true` if the ID should be updated,
     * `false` otherwise.
     * @return the packet.
     * @throws IndexOutOfBoundsException
     * if `updateId` is `true` and the new
     * buffer has less than `1` readable
     * `byte`.
     * @see .setBuffer
     */
    @Throws(IndexOutOfBoundsException::class)
    fun setBuffer(buffer: ByteBuf?, updateId: Boolean): RakNetPacket {
        super.setBuffer(buffer)
        if (updateId) {
            id = readUnsignedByte()
        }
        return this
    }

    /**
     * Updates the buffer.
     *
     * @param datagram
     * the [DatagramPacket] whose buffer to read from and write
     * to.
     * @param updateId
     * `true` if the ID should be updated,
     * `false` otherwise.
     * @return the packet.
     * @throws NullPointerException
     * if the `datagram` packet is `null`.
     * @throws IndexOutOfBoundsException
     * if `updateId` is `true` and the new
     * buffer has less than `1` readable
     * `byte`.
     * @see .setBuffer
     */
    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun setBuffer(datagram: DatagramPacket?, updateId: Boolean): RakNetPacket {
        if (datagram == null) {
            throw NullPointerException("Datagram packet cannot be null")
        }
        return this.setBuffer(datagram.content(), updateId)
    }

    /**
     * Updates the buffer.
     *
     * @param data
     * the `byte[]` to create the new buffer from.
     * @param updateId
     * `true` if the ID should be updated,
     * `false` otherwise.
     * @return the packet.
     * @throws NullPointerException
     * if the `data` is `null`.
     * @throws IndexOutOfBoundsException
     * if `updateId` is `true` and the new
     * buffer has less than `1` readable
     * `byte`.
     * @see .setBuffer
     */
    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun setBuffer(data: ByteArray?, updateId: Boolean): RakNetPacket {
        return this.setBuffer(Unpooled.copiedBuffer(data), updateId)
    }

    /**
     * Updates the buffer.
     *
     * @param packet
     * the packet whose buffer to copy to read from and write to.
     * @param updateId
     * `true` if the ID should be updated,
     * `false` otherwise.
     * @return the packet.
     * @throws IndexOutOfBoundsException
     * if `updateId` is `true` and the new
     * buffer has less than `1` readable
     * `byte`.
     * @see .setBuffer
     */
    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun setBuffer(packet: Packet, updateId: Boolean): RakNetPacket {
        return this.setBuffer(packet.copy(), updateId)
    }

    /**
     * Flips the packet.
     *
     * @param updateId
     * `true` if ID should be updated, `false`
     * otherwise.
     * @return the packet.
     * @throws IndexOutOfBoundsException
     * if `updateId` is `true` and the buffer
     * has less than `1` readable `byte`.
     * @see .flip
     */
    @Throws(IndexOutOfBoundsException::class)
    fun flip(updateId: Boolean): RakNetPacket {
        super.flip()
        if (updateId == true) {
            id = readUnsignedByte()
        }
        return this
    }

    /**
     * {@inheritDoc} After the packet has been flipped, an unsigined
     * `byte` will be read to get the ID.
     *
     * @throws IndexOutOfBoundsException
     * if the buffer has less than `1` readable
     * `byte`.
     */
    @Throws(IndexOutOfBoundsException::class)
    override fun flip(): Packet {
        return this.flip(true)
    }

    override fun toString(): String {
        return "RakNetPacket [id=" + id + ", size()=" + size() + ", remaining()=" + remaining() + "]"
    }

    companion object {
        /**
         * The name of the `encode()` method.
         */
        private const val ENCODE_METHOD_NAME = "encode"

        /**
         * The name of the `decode()` method.
         */
        private const val DECODE_METHOD_NAME = "decode"

        /**
         * The magic identifier.
         */
        val MAGIC = byteArrayOf(
            0x00.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0x00,
            0xFE.toByte(),
            0xFE.toByte(),
            0xFE.toByte(),
            0xFE.toByte(),
            0xFD.toByte(),
            0xFD.toByte(),
            0xFD.toByte(),
            0xFD.toByte(),
            0x12.toByte(),
            0x34.toByte(),
            0x56.toByte(),
            0x78.toByte()
        )

        /**
         * Returns whether or not a method with the specified name has been
         * overridden the method in the original specified class by the specified
         * class instance.
         *
         * @param instance
         * the class instance.
         * @param clazz
         * the original class.
         * @param methodName
         * the name of the method.
         * @return `true` if the method has been overridden,
         * `false` otherwise.
         */
        private fun isMethodOverriden(instance: Class<*>?, clazz: Class<*>?, methodName: String?): Boolean {
            return try {
                if (instance == null || clazz == null || methodName == null) {
                    false // Not enough information to compare
                } else clazz.getMethod(methodName).declaringClass != clazz
            } catch (e: NoSuchMethodException) {
                false
            } catch (e: SecurityException) {
                false
            }
        }
    }
}