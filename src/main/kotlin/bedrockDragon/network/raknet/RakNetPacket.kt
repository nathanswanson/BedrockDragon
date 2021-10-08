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

import bedrockDragon.network.raknet.map.ShortMap
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
        this.writeB(*MAGIC)
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
        this.writeB(*ConnectionType.MAGIC)
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
         * The cached packet names, mapped by their ID.
         */
        private val PACKET_NAMES = ShortMap<String>()

        /**
         * The cached packet IDs, mapped by their name.
         */
        private val PACKET_IDS = HashMap<String, Short>()

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
         * The ID of the [ CONNECTED_PING][bedrockDragon.network.raknet.protocol.status.ConnectedPing] packet.
         */
        const val ID_CONNECTED_PING: Short = 0x00

        /**
         * The ID of the [ UNCONNECTED_PING][bedrockDragon.network.raknet.protocol.status.UnconnectedPing] packet.
         */
        const val ID_UNCONNECTED_PING: Short = 0x01

        /**
         * The ID of the
         * [ UNCONNECTED_PING_OPEN_CONNECTIONS][bedrockDragon.network.raknet.protocol.status.UnconnectedPingOpenConnections] packet.
         */
        const val ID_UNCONNECTED_PING_OPEN_CONNECTIONS: Short = 0x02

        /**
         * The ID of the [ CONNECTED_PONG][bedrockDragon.network.raknet.protocol.status.ConnectedPong] packet.
         */
        const val ID_CONNECTED_PONG: Short = 0x03

        /**
         * The ID of the `DETECT_LOST_CONNECTIONS` packet.
         */
        const val ID_DETECT_LOST_CONNECTIONS: Short = 0x04

        /**
         * The ID of the
         * [ OPEN_CONNECTION_REQUEST_1][bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestOne] packet.
         */
        const val ID_OPEN_CONNECTION_REQUEST_1: Short = 0x05

        /**
         * The ID of the
         * [ OPEN_CONNECTION_REPLY_1][bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseOne] packet.
         */
        const val ID_OPEN_CONNECTION_REPLY_1: Short = 0x06

        /**
         * The ID of the
         * [ OPEN_CONNECTION_REQUEST_2][bedrockDragon.network.raknet.protocol.connection.OpenConnectionRequestTwo] packet.
         */
        const val ID_OPEN_CONNECTION_REQUEST_2: Short = 0x07

        /**
         * The ID of the
         * [ OPEN_CONNECTION_REPLY_2][bedrockDragon.network.raknet.protocol.connection.OpenConnectionResponseTwo] packet.
         */
        const val ID_OPEN_CONNECTION_REPLY_2: Short = 0x08

        /**
         * The ID of the [ CONNECTION_REQUEST][bedrockDragon.network.raknet.protocol.login.ConnectionRequest] packet.
         */
        const val ID_CONNECTION_REQUEST: Short = 0x09

        /**
         * The ID of the `REMOVE_SYSTEM_REQUIRES_PUBLIC_KEY` packet.
         */
        const val ID_REMOTE_SYSTEM_REQUIRES_PUBLIC_KEY: Short = 0x0A

        /**
         * The ID of the `OUR_SYSTEM_REQUIRES_SECURITY` packet.
         */
        const val ID_OUR_SYSTEM_REQUIRES_SECURITY: Short = 0x0B

        /**
         * The ID of the `PUBLIC_KEY_MISMATCH` packet.
         */
        const val ID_PUBLIC_KEY_MISMATCH: Short = 0x0C

        /**
         * The ID of the `OUT_OF_BAND_INTERNAL` packet.
         */
        const val ID_OUT_OF_BAND_INTERNAL: Short = 0x0D

        /**
         * The ID of the `SND_RECEIPT_ACKED` packet.
         *
         *
         * In the original implementation of RakNet, when a packet is acknowledged
         * by a peer this packet is sent back through loopback to the original
         * sender of the packet with the acknoweldgeable reliability. Since this
         * implementation has listeners will special built in acknowledgement and
         * loss methods, this packet has no need for implementation.
         */
        const val ID_SND_RECEIPT_ACKED: Short = 0x0E

        /**
         * The ID of the `SND_RECEIPT_LOSS` packet.
         *
         *
         * In the original implementation of RakNet, when a packet is acknowledged
         * by a peer this packet is sent back through loopback to the original
         * sender of the packet with the acknoweldgeable reliability. Since this
         * implementation has listeners will special built in acknowledgement and
         * loss methods, this packet has no need for implementation.
         */
        const val ID_SND_RECEIPT_LOSS: Short = 0x0F

        /**
         * The ID of the
         * [ CONNECTION_REQUEST_ACCEPTED][bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted] packet.
         */
        const val ID_CONNECTION_REQUEST_ACCEPTED: Short = 0x10

        /**
         * The ID of the `CONNECTION_ATTEMPT_FAILED` packet.
         */
        const val ID_CONNECTION_ATTEMPT_FAILED: Short = 0x11

        /**
         * The ID of the `ALREADY_CONNECTED` packet.
         */
        const val ID_ALREADY_CONNECTED: Short = 0x12

        /**
         * The ID of the
         * [ NEW_INCOMING_CONNECTION][bedrockDragon.network.raknet.protocol.login.NewIncomingConnection] packet.
         */
        const val ID_NEW_INCOMING_CONNECTION: Short = 0x13

        /**
         * The ID of the `NO_FREE_INCOMING_CONNECTIONS` packet.
         */
        const val ID_NO_FREE_INCOMING_CONNECTIONS: Short = 0x14

        /**
         * The ID of the `DISCONNECTION_NOTIFICATION` packet.
         */
        const val ID_DISCONNECTION_NOTIFICATION: Short = 0x15

        /**
         * The ID of the `CONNECTION_LOST` packet.
         */
        const val ID_CONNECTION_LOST: Short = 0x16

        /**
         * The ID of the
         * [ CONNECTION_BANNED][bedrockDragon.network.raknet.protocol.connection.ConnectionBanned] packet.
         */
        const val ID_CONNECTION_BANNED: Short = 0x17

        /**
         * The ID of the `INVALID_PASSWORD` packet.
         */
        const val ID_INVALID_PASSWORD: Short = 0x18

        /**
         * The ID of the
         * [ INCOMPATIBLE_PROTOCOL_VERSION][bedrockDragon.network.raknet.protocol.connection.IncompatibleProtocolVersion] packet.
         */
        const val ID_INCOMPATIBLE_PROTOCOL_VERSION: Short = 0x19

        /**
         * The ID of the `IP_RECENTLY_CONNECTED` packet.
         */
        const val ID_IP_RECENTLY_CONNECTED: Short = 0x1A

        /**
         * The ID of the `TIMESTAMP` packet.
         */
        const val ID_TIMESTAMP: Short = 0x1B

        /**
         * The ID of the [ UNCONNECTED_PONG][bedrockDragon.network.raknet.protocol.status.UnconnectedPong] packet.
         */
        const val ID_UNCONNECTED_PONG: Short = 0x1C

        /**
         * The ID of the `ADVERTISE_SYSTEM` packet.
         */
        const val ID_ADVERTISE_SYSTEM: Short = 0x1D

        /**
         * The ID of the `DOWNLOAD_PROGRESS` packet.
         */
        const val ID_DOWNLOAD_PROGRESS: Short = 0x1E

        /**
         * The ID of the `REMOTE_DISCONNECTION_NOTIFICATION` packet.
         */
        const val ID_REMOTE_DISCONNECTION_NOTIFICATION: Short = 0x1F

        /**
         * The ID of the `REMOTE_CONNECTION_LOST` packet.
         */
        const val ID_REMOTE_CONNECTION_LOST: Short = 0x20

        /**
         * The ID of the `REMOTE_NEW_INCOMING_CONNECTION` packet.
         */
        const val ID_REMOTE_NEW_INCOMING_CONNECTION: Short = 0x21

        /**
         * The ID of the `FILE_LIST_TRANSFER_HEADER` packet.
         */
        const val ID_FILE_LIST_TRANSFER_HEADER: Short = 0x22

        /**
         * The ID of the `FILE_LIST_TRANSFER_FILE` packet.
         */
        const val ID_FILE_LIST_TRANSFER_FILE: Short = 0x23

        /**
         * The ID of the `FILE_LIST_REFERENCE_PUSH_ACK` packet.
         */
        const val ID_FILE_LIST_REFERENCE_PUSH_ACK: Short = 0x24

        /**
         * The ID of the `DDT_DOWNLOAD_REQUEST` packet.
         */
        const val ID_DDT_DOWNLOAD_REQUEST: Short = 0x25

        /**
         * The ID of the `TRANSPORT_STRING` packet.
         */
        const val ID_TRANSPORT_STRING: Short = 0x26

        /**
         * The ID of the `REPLICA_MANAGER_CONSTRUCTION` packet.
         */
        const val ID_REPLICA_MANAGER_CONSTRUCTION: Short = 0x27

        /**
         * The ID of the `REPLICA_MANAGER_SCOPE_CHANGE` packet.
         */
        const val ID_REPLICA_MANAGER_SCOPE_CHANGE: Short = 0x28

        /**
         * The ID of the `REPLICA_MANAGER_SERIALIZE` packet.
         */
        const val ID_REPLICA_MANAGER_SERIALIZE: Short = 0x29

        /**
         * The ID of the `REPLICA_MANAGER_DOWNLOAD_STARTED` packet.
         */
        const val ID_REPLICA_MANAGER_DOWNLOAD_STARTED: Short = 0x2A

        /**
         * The ID of the `REPLICA_MANAGER_DOWNLOAD_COMPLETE` packet.
         */
        const val ID_REPLICA_MANAGER_DOWNLOAD_COMPLETE: Short = 0x2B

        /**
         * The ID of the `RAKVOICE_OPEN_CHANNEL_REQUEST` packet.
         */
        const val ID_RAKVOICE_OPEN_CHANNEL_REQUEST: Short = 0x2C

        /**
         * The ID of the `RAKVOICE_OPEN_CHANNEL_REPLY` packet.
         */
        const val ID_RAKVOICE_OPEN_CHANNEL_REPLY: Short = 0x2D

        /**
         * The ID of the `RAKVOICE_CLOSE_CHANNEL` packet.
         */
        const val ID_RAKVOICE_CLOSE_CHANNEL: Short = 0x2E

        /**
         * The ID of the `RAKVOICE_DATA` packet.
         */
        const val ID_RAKVOICE_DATA: Short = 0x2F

        /**
         * The ID of the `AUTOPATHER_GET_CHANGELIST_SINCE_DATE` packet.
         */
        const val ID_AUTOPATCHER_GET_CHANGELIST_SINCE_DATE: Short = 0x30

        /**
         * The ID of the `AUTOPATCHER_CREATION_LIST` packet.
         */
        const val ID_AUTOPATCHER_CREATION_LIST: Short = 0x31

        /**
         * The ID of the `AUTOPATCHER_DELETION_LIST` packet.
         */
        const val ID_AUTOPATCHER_DELETION_LIST: Short = 0x32

        /**
         * The ID of the `AUTOPATCHER_GET_PATCH` packet.
         */
        const val ID_AUTOPATCHER_GET_PATCH: Short = 0x33

        /**
         * The ID of the `AUTOPATCHER_PATCH_LIST` packet.
         */
        const val ID_AUTOPATCHER_PATCH_LIST: Short = 0x34

        /**
         * The ID of the `AUTOPATHER_REPOSITORY_FATAL_ERROR` packet.
         */
        const val ID_AUTOPATCHER_REPOSITORY_FATAL_ERROR: Short = 0x35

        /**
         * The ID of the
         * `AUTOPATHER_CANNOT_DOWNLOAD_ORIGINAL_UNMODIFIED_FILES` packet.
         */
        const val ID_AUTOPATCHER_CANNOT_DOWNLOAD_ORIGINAL_UNMODIFIED_FILES: Short = 0x36

        /**
         * The ID of the `AUTOPATHER_FINISHED_INTERNAL` packet.
         */
        const val ID_AUTOPATCHER_FINISHED_INTERNAL: Short = 0x37

        /**
         * The ID of the `AUTOPATHER_FINISHED` packet.
         */
        const val ID_AUTOPATCHER_FINISHED: Short = 0x38

        /**
         * The ID of the `AUTOPATCHER_RESTART_APPLICATION` packet.
         */
        const val ID_AUTOPATCHER_RESTART_APPLICATION: Short = 0x39

        /**
         * The ID of the `NAT_PUNCHTHROUGH_REQUEST` packet.
         */
        const val ID_NAT_PUNCHTHROUGH_REQUEST: Short = 0x3A

        /**
         * The ID of the `NAT_CONNECT_AT_TIME` packet.
         */
        const val ID_NAT_CONNECT_AT_TIME: Short = 0x3B

        /**
         * The ID of the `NAT_GET_MOST_RECENT_PORT` packet.
         */
        const val ID_NAT_GET_MOST_RECENT_PORT: Short = 0x3C

        /**
         * The ID of the `NAT_CLIENT_READY` packet.
         */
        const val ID_NAT_CLIENT_READY: Short = 0x3D

        /**
         * The ID of the `NAT_TARGET_NOT_CONNECT` packet.
         */
        const val ID_NAT_TARGET_NOT_CONNECTED: Short = 0x3E

        /**
         * The ID of the `NAT_TARGET_UNRESPONSIVE` packet.
         */
        const val ID_NAT_TARGET_UNRESPONSIVE: Short = 0x3F

        /**
         * The ID of the `NAT_CONNECTION_TO_TARGET_LOST` packet.
         */
        const val ID_NAT_CONNECTION_TO_TARGET_LOST: Short = 0x40

        /**
         * The ID of the `NAT_ALREADY_IN_PROGRESS` packet.
         */
        const val ID_NAT_ALREADY_IN_PROGRESS: Short = 0x41

        /**
         * The ID of the `NAT_PUNCHTHROUGH_FAILED` packet.
         */
        const val ID_NAT_PUNCHTHROUGH_FAILED: Short = 0x42

        /**
         * The ID of the `NAT_PUNCHTHROUGH_SUCCEEDED` packet.
         */
        const val ID_NAT_PUNCHTHROUGH_SUCCEEDED: Short = 0x43

        /**
         * The ID of the `READY_EVENT_SET` packet.
         */
        const val ID_READY_EVENT_SET: Short = 0x44

        /**
         * The ID of the `READY_EVENT_UNSET` packet.
         */
        const val ID_READY_EVENT_UNSET: Short = 0x45

        /**
         * The ID of the `READY_EVENT_ALL_SET` packet.
         */
        const val ID_READY_EVENT_ALL_SET: Short = 0x46

        /**
         * The ID of the `READY_EVENT_QUERY` packet.
         */
        const val ID_READY_EVENT_QUERY: Short = 0x47

        /**
         * The ID of the `LOBBY_GENERAL` packet.
         */
        const val ID_LOBBY_GENERAL: Short = 0x48

        /**
         * The ID of the `RPC_REMOTE_ERROR` packet.
         */
        const val ID_RPC_REMOTE_ERROR: Short = 0x49

        /**
         * The ID of the `RPC_PLUGIN` packet.
         */
        const val ID_RPC_PLUGIN: Short = 0x4A

        /**
         * The ID of the `FILE_LIST_REFERENCE_PUSH` packet.
         */
        const val ID_FILE_LIST_REFERENCE_PUSH: Short = 0x4B

        /**
         * The ID of the `READY_EVENT_FORCE_ALL_SET` packet.
         */
        const val ID_READY_EVENT_FORCE_ALL_SET: Short = 0x4C

        /**
         * The ID of the `ROOMS_EXECUTE_FUNC` packet.
         */
        const val ID_ROOMS_EXECUTE_FUNC: Short = 0x4D

        /**
         * The ID of the `ROOMS_LOGON_STATUS` packet.
         */
        const val ID_ROOMS_LOGON_STATUS: Short = 0x4E

        /**
         * The ID of the `ROOMS_HANDLE_CHANGE` packet.
         */
        const val ID_ROOMS_HANDLE_CHANGE: Short = 0x4F

        /**
         * The ID of the `LOBBY2_SEND_MESSAGE` packet.
         */
        const val ID_LOBBY2_SEND_MESSAGE: Short = 0x50

        /**
         * The ID of the `LOBBY2_SERVER_ERROR` packet.
         */
        const val ID_LOBBY2_SERVER_ERROR: Short = 0x51

        /**
         * The ID of the `FMC2_NEW_HOST` packet.
         */
        const val ID_FCM2_NEW_HOST: Short = 0x52

        /**
         * The ID of the `FCM2_REQUEST_FCMGUID` packet.
         */
        const val ID_FCM2_REQUEST_FCMGUID: Short = 0x53

        /**
         * The ID of the `FCM2_RESPOND_CONNECTION_COUNT` packet.
         */
        const val ID_FCM2_RESPOND_CONNECTION_COUNT: Short = 0x54

        /**
         * The ID of the `FMC2_INFORM_FCMGUID` packet.
         */
        const val ID_FCM2_INFORM_FCMGUID: Short = 0x55

        /**
         * The ID of the `FCM2_UPDATE_MIN_TOTAL_CONNECTION_COUNT` packet.
         */
        const val ID_FCM2_UPDATE_MIN_TOTAL_CONNECTION_COUNT: Short = 0x56

        /**
         * The ID of the `FCM2_VERIFIED_JOIN_START` packet.
         */
        const val ID_FCM2_VERIFIED_JOIN_START: Short = 0x57

        /**
         * The ID of the `FCM2_VERIFIED_JOIN_CAPABLE` packet.
         */
        const val ID_FCM2_VERIFIED_JOIN_CAPABLE: Short = 0x58

        /**
         * The ID of the `FCM2_VERIFIED_JOIN_FAILED` packet.
         */
        const val ID_FCM2_VERIFIED_JOIN_FAILED: Short = 0x59

        /**
         * The ID of the `FCM2_VERIFIED_JOIN_ACCEPTED` packet.
         */
        const val ID_FCM2_VERIFIED_JOIN_ACCEPTED: Short = 0x5A

        /**
         * The ID of the `FCM2_VERIFIED_JOIN_REJECTED` packet.
         */
        const val ID_FCM2_VERIFIED_JOIN_REJECTED: Short = 0x5B

        /**
         * The ID of the `UDP_PROXY_GENERAL` packet.
         */
        const val ID_UDP_PROXY_GENERAL: Short = 0x5C

        /**
         * The ID of the `SQLITE3_EXEC` packet.
         */
        const val ID_SQLITE3_EXEC: Short = 0x5D

        /**
         * The ID of the `SQLITE3_UNKNOWN_DB` packet.
         */
        const val ID_SQLITE3_UNKNOWN_DB: Short = 0x5E

        /**
         * The ID of the `SQLLITE_LOGGER` packet.
         */
        const val ID_SQLLITE_LOGGER: Short = 0x5F

        /**
         * The ID of the `NAT_TYPE_DETECTION_REQUEST` packet.
         */
        const val ID_NAT_TYPE_DETECTION_REQUEST: Short = 0x60

        /**
         * The ID of the `NAT_TYPE_DETECTION_RESULT` packet.
         */
        const val ID_NAT_TYPE_DETECTION_RESULT: Short = 0x61

        /**
         * The ID of the `ROUTER_2_INTERNAL` packet.
         */
        const val ID_ROUTER_2_INTERNAL: Short = 0x62

        /**
         * The ID of the `ROUTER_2_FOWARDING_NO_PATH` packet.
         */
        const val ID_ROUTER_2_FORWARDING_NO_PATH: Short = 0x63

        /**
         * The ID of the `ROUTER_2_FORWARDING_ESTABLISHED` packet.
         */
        const val ID_ROUTER_2_FORWARDING_ESTABLISHED: Short = 0x64

        /**
         * The ID of the `ROUTER_2_REROUTED` packet.
         */
        const val ID_ROUTER_2_REROUTED: Short = 0x65

        /**
         * The ID of the `TEAM_BALANCER_INTERNAL` packet.
         */
        const val ID_TEAM_BALANCER_INTERNAL: Short = 0x66

        /**
         * The ID of the `TEAM_BALANCER_REQUESTED_TEAM_FULL` packet.
         */
        const val ID_TEAM_BALANCER_REQUESTED_TEAM_FULL: Short = 0x67

        /**
         * The ID of the `TEAM_BALANCER_REQUESTED_TEAM_LOCKED` packet.
         */
        const val ID_TEAM_BALANCER_REQUESTED_TEAM_LOCKED: Short = 0x68

        /**
         * The ID of the `TEAM_BALANCER_TEAM_REQUESTED_CANCELLED` packet.
         */
        const val ID_TEAM_BALANCER_TEAM_REQUESTED_CANCELLED: Short = 0x69

        /**
         * The ID of the `TEAM_BALANCER_TEAM_ASSIGNED` packet.
         */
        const val ID_TEAM_BALANCER_TEAM_ASSIGNED: Short = 0x6A

        /**
         * The ID of the `LIGHTSPEED_INTEGRATION` packet.
         */
        const val ID_LIGHTSPEED_INTEGRATION: Short = 0x6B

        /**
         * The ID of the `XBOX_LOBBY` packet.
         */
        const val ID_XBOX_LOBBY: Short = 0x6C

        /**
         * The ID of the
         * `TWO_WAY_AUTHENTICATION_INCOMING_CHALLENEGE_SUCCESS` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_INCOMING_CHALLENGE_SUCCESS: Short = 0x6D

        /**
         * The ID of the
         * `TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_SUCCESS` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_SUCCESS: Short = 0x6E

        /**
         * The ID of the
         * `TWO_WAY_AUTHENTICATION_INCOMING_CHALLENGE_FAILURE` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_INCOMING_CHALLENGE_FAILURE: Short = 0x6F

        /**
         * The ID of the
         * `TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_FAILURE` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_FAILURE: Short = 0x70

        /**
         * The ID of the
         * `TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_TIMEOUT` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_OUTGOING_CHALLENGE_TIMEOUT: Short = 0x71

        /**
         * The ID of the `TWO_WAY_AUTHENTICATION_NEGOTIATION` packet.
         */
        const val ID_TWO_WAY_AUTHENTICATION_NEGOTIATION: Short = 0x72

        /**
         * The ID of the `CLOUD_POST_REQUEST` packet.
         */
        const val ID_CLOUD_POST_REQUEST: Short = 0x73

        /**
         * The ID of the `CLOUD_RELEASE_REQUEST` packet.
         */
        const val ID_CLOUD_RELEASE_REQUEST: Short = 0x74

        /**
         * The ID of the `CLOUD_GET_REQUEST` packet.
         */
        const val ID_CLOUD_GET_REQUEST: Short = 0x75

        /**
         * The ID of the `CLOUD_GET_RESPONSE` packet.
         */
        const val ID_CLOUD_GET_RESPONSE: Short = 0x76

        /**
         * The ID of the `CLOUD_UNSUBSCRIBE_REQUEST` packet.
         */
        const val ID_CLOUD_UNSUBSCRIBE_REQUEST: Short = 0x77

        /**
         * The ID of the `CLOUD_SERVER_TO_SERVER_COMMAND` packet.
         */
        const val ID_CLOUD_SERVER_TO_SERVER_COMMAND: Short = 0x78

        /**
         * The ID of the `CLOUD_SUBSCRIPTION_NOTIFICATION` packet.
         */
        const val ID_CLOUD_SUBSCRIPTION_NOTIFICATION: Short = 0x79

        /**
         * The ID of the `LIB_VOICE` packet.
         */
        const val ID_LIB_VOICE: Short = 0x7A

        /**
         * The ID of the `RELAY_PLUGIN` packet.
         */
        const val ID_RELAY_PLUGIN: Short = 0x7B

        /**
         * The ID of the `NAT_REQUEST_BOUND_ADDRESSES` packet.
         */
        const val ID_NAT_REQUEST_BOUND_ADDRESSES: Short = 0x7C

        /**
         * The ID of the `NAT_RESPOND_BOUND_ADDRESSES` packet.
         */
        const val ID_NAT_RESPOND_BOUND_ADDRESSES: Short = 0x7D

        /**
         * The ID of the `FCM2_UPDATE_USER_CONTENT` packet.
         */
        const val ID_FCM2_UPDATE_USER_CONTEXT: Short = 0x7E

        /**
         * The ID of the `RESERVED_3` packet.
         */
        const val ID_RESERVED_3: Short = 0x7F

        /**
         * The ID of the `RESERVED_4` packet.
         */
        const val ID_RESERVED_4: Short = 0x80

        /**
         * The ID of the `RESERVED_5` packet.
         */
        const val ID_RESERVED_5: Short = 0x81

        /**
         * The ID of the `RESERVED_6` packet.
         */
        const val ID_RESERVED_6: Short = 0x82

        /**
         * The ID of the `RESERVERD_7` packet.
         */
        const val ID_RESERVED_7: Short = 0x83

        /**
         * The ID of the `RESERVED_8` packet.
         */
        const val ID_RESERVED_8: Short = 0x84

        /**
         * The ID of the `RESERVED_9` packet.
         */
        const val ID_RESERVED_9: Short = 0x85

        /**
         * This is the first ID that the user can for the IDs of their packets.
         * Since packet IDs are written using [.writeUnsignedByte], the
         * highest packet ID that can be used is `0xFF`.
         *
         *
         * If one must have more than `121` packet IDs
         * (`0xFF - ID_USER_PACKET_ENUM`), then one can have a singular
         * ID that they use for all of their user packets with another field to be
         * the packet ID.
         */
        const val ID_USER_PACKET_ENUM: Short = 0x86

        /**
         * The ID of the `CUSTOM_0` packet.
         */
        const val ID_CUSTOM_0: Short = 0x80

        /**
         * The ID of the `CUSTOM_1` packet.
         */
        const val ID_CUSTOM_1: Short = 0x81

        /**
         * The ID of the `CUSTOM_2` packet.
         */
        const val ID_CUSTOM_2: Short = 0x82

        /**
         * The ID of the `CUSTOM_3` packet.
         */
        const val ID_CUSTOM_3: Short = 0x83

        /**
         * The ID of the
         * [CUSTOM_4][bedrockDragon.network.raknet.protocol.message.CustomFourPacket]
         * packet.
         */
        const val ID_CUSTOM_4: Short = 0x84

        /**
         * The ID of the `CUSTOM_5` packet.
         */
        const val ID_CUSTOM_5: Short = 0x85

        /**
         * The ID of the `CUSTOM_6` packet.
         */
        const val ID_CUSTOM_6: Short = 0x86

        /**
         * The ID of the `CUSTOM_7` packet.
         */
        const val ID_CUSTOM_7: Short = 0x87

        /**
         * The ID of the `CUSTOM_8` packet.
         */
        const val ID_CUSTOM_8: Short = 0x88

        /**
         * The ID of the `CUSTOM_9` packet.
         */
        const val ID_CUSTOM_9: Short = 0x89

        /**
         * The ID of the `CUSTOM_A` packet.
         */
        const val ID_CUSTOM_A: Short = 0x8A

        /**
         * The ID of the `CUSTOM_B` packet.
         */
        const val ID_CUSTOM_B: Short = 0x8B

        /**
         * The ID of the `CUSTOM_C` packet.
         */
        const val ID_CUSTOM_C: Short = 0x8C

        /**
         * The ID of the `CUSTOM_D` packet.
         */
        const val ID_CUSTOM_D: Short = 0x8D

        /**
         * The ID of the `CUSTOM_E` packet.
         */
        const val ID_CUSTOM_E: Short = 0x8E

        /**
         * The ID of the `CUSTOM_F` packet.
         */
        const val ID_CUSTOM_F: Short = 0x8F

        /**
         * The ID of the
         * [ ACK][bedrockDragon.network.raknet.protocol.message.acknowledge.AcknowledgedPacket] packet.
         */
        const val ID_ACK: Short = 0xC0

        /**
         * The ID of the
         * [ NACK][bedrockDragon.network.raknet.protocol.message.acknowledge.NotAcknowledgedPacket] packet.
         */
        const val ID_NACK: Short = 0xA0
        private var mappedNameIds = false

        /**
         * Maps all `public` packet IDs to their respective field names
         * and vice-versa.
         *
         *
         * Packet IDs [.ID_CUSTOM_0], [.ID_CUSTOM_1],
         * [.ID_CUSTOM_2], [.ID_CUSTOM_3], [.ID_CUSTOM_4],
         * [.ID_CUSTOM_5], [.ID_CUSTOM_6], [.ID_CUSTOM_7],
         * [.ID_CUSTOM_8], [.ID_CUSTOM_9], [.ID_CUSTOM_A],
         * [.ID_CUSTOM_B], [.ID_CUSTOM_C], [.ID_CUSTOM_D],
         * [.ID_CUSTOM_E], [.ID_CUSTOM_F], [.ID_ACK], and
         * [.ID_NACK] are ignored as they are not only internal packets but
         * they also override other packets with the same ID.
         */
        private fun mapNameIds() {
            if (mappedNameIds == false) {
                //Logger log = LogManager.getLogger(RakNetPacket.class);
                for (field in RakNetPacket::class.java.fields) {
                    if (field.type == Short::class.javaPrimitiveType) {
                        try {
                            val packetId = field.getShort(null)
                            if (packetId >= ID_CUSTOM_0 && packetId <= ID_CUSTOM_F || packetId == ID_ACK || packetId == ID_NACK) {
                                continue  // Ignored as they override other packet
                                // IDs
                            }
                            val packetName = field.name
                            val currentName = PACKET_NAMES.put(packetId, packetName)
                            PACKET_IDS[packetName] = packetId
                            if (currentName != null) {
                                if (currentName != packetName) {
                                    //log.warn("Found duplicate ID " + RakNet.toHexStringId(packetId) + " for \"" + packetName
                                    //		+ "\" and \"" + currentName + "\", overriding name and ID");
                                }
                            } else {
                                //log.debug("Assigned packet ID " + RakNet.toHexStringId(packetId) + " to " + packetName);
                            }
                        } catch (e: ReflectiveOperationException) {
                            e.printStackTrace()
                        }
                    }
                }
                mappedNameIds = true
            }
        }

        /**
         * Returns whether or not a packet with the specified ID exists as a RakNet
         * packet.
         *
         * @param id
         * the ID of the packet.
         * @return `true` if a packet with the ID exists as a RakNet
         * packet, `false`.
         */
        fun hasPacket(id: Int): Boolean {
            if (mappedNameIds == false) {
                mapNameIds()
            }
            return PACKET_NAMES.containsKey(id.toShort())
        }

        /**
         * Returns whether or not the specified packet exists as a RakNet packet.
         *
         * @param packet
         * the packet.
         * @return `true` if the specified packet exists as a RakNet
         * packet, `false`.
         */
        fun hasPacket(packet: RakNetPacket?): Boolean {
            return if (packet == null) {
                false
            } else hasPacket(packet.id.toInt())
        }

        /**
         * Returns whether or not a packet with the specified name exists as a
         * RakNet packet.
         *
         * @param name
         * the name of the packet.
         * @return `true` if a packet with the name exists as a RakNet
         * packet, `false`.
         */
        fun hasPacket(name: String): Boolean {
            if (mappedNameIds == false) {
                mapNameIds()
            }
            return PACKET_IDS.containsKey(name)
        }

        /**
         * Returns the name of the packet with the specified ID.
         *
         *
         * Packet IDs [.ID_CUSTOM_0], [.ID_CUSTOM_1],
         * [.ID_CUSTOM_2], [.ID_CUSTOM_3], [.ID_CUSTOM_4],
         * [.ID_CUSTOM_5], [.ID_CUSTOM_6], [.ID_CUSTOM_7],
         * [.ID_CUSTOM_8], [.ID_CUSTOM_9], [.ID_CUSTOM_A],
         * [.ID_CUSTOM_B], [.ID_CUSTOM_C], [.ID_CUSTOM_D],
         * [.ID_CUSTOM_E], [.ID_CUSTOM_F], [.ID_ACK], and
         * [.ID_NACK] will never be returned as they are not only internal
         * packets but they also override other packets with the same ID.
         *
         * @param id
         * the ID of the packet.
         * @return the name of the packet with the specified ID, its hexadecimal ID
         * according to [RakNet.toHexStringId] if it does not
         * exist.
         */
        fun getName(id: Int): String? {
            if (mappedNameIds == false) {
                mapNameIds()
            }
            return if (!PACKET_NAMES.containsKey(id.toShort())) {
                RakNet.toHexStringId(id and 0xFF)
            } else PACKET_NAMES[id.toShort()]
        }

        /**
         * Returns the name of the specified packet.
         *
         *
         * Packet IDs [.ID_CUSTOM_0], [.ID_CUSTOM_1],
         * [.ID_CUSTOM_2], [.ID_CUSTOM_3], [.ID_CUSTOM_4],
         * [.ID_CUSTOM_5], [.ID_CUSTOM_6], [.ID_CUSTOM_7],
         * [.ID_CUSTOM_8], [.ID_CUSTOM_9], [.ID_CUSTOM_A],
         * [.ID_CUSTOM_B], [.ID_CUSTOM_C], [.ID_CUSTOM_D],
         * [.ID_CUSTOM_E], [.ID_CUSTOM_F], [.ID_ACK], and
         * [.ID_NACK] will never be returned as they are not only internal
         * packets but they also override other packets with the same ID.
         *
         * @param packet
         * the packet.
         * @return the name of the packet, its hexadecimal ID according to
         * [RakNet.toHexStringId] if it does not exist.
         */
        fun getName(packet: RakNetPacket?): String? {
            return if (packet == null) {
                null
            } else getName(packet.id.toInt())
        }

        /**
         * Returns the ID of the packet with the specified name.
         *
         * @param name
         * the name of the packet.
         * @return the ID of the packet with the specified name, `-1` if
         * it does not exist.
         */
        fun getId(name: String): Short {
            if (mappedNameIds == false) {
                mapNameIds()
            }
            return if (PACKET_IDS.containsKey(name)) PACKET_IDS[name]!! else -1
        }

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