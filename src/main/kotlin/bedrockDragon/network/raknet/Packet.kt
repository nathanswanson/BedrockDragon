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

import bedrockDragon.item.Item
import bedrockDragon.network.raknet.stream.PacketDataInputStream
import bedrockDragon.network.raknet.stream.PacketDataOutputStream
import bedrockDragon.world.PaletteGlobal
import dev.romainguy.kotlin.math.Float3
import io.netty.buffer.ByteBuf
import io.netty.buffer.EmptyByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import java.io.EOFException
import java.io.IOException
import java.math.BigInteger
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.UnknownHostException
import java.util.*
import kotlin.experimental.and

/**
 * A generic packet that has the ability to read and write data to and from a
 * source buffer.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
open class Packet @JvmOverloads constructor(buffer: ByteBuf? =  /* Solves ambiguity */null as ByteBuf?) {
    private var buffer: ByteBuf

    /**
     * Returns the packet's [InputStream][java.io.InputStream]
     *
     * @return the packet's [InputStream][java.io.InputStream].
     */
    val inputStream: PacketDataInputStream

    /**
     * Returns the packet's [OutputStream][java.io.OutputStream].
     *
     * @return the packet's [OutputStream][java.io.OutputStream].
     */
    val outputStream: PacketDataOutputStream

    /**
     * Creates packet from an existing [DatagramPacket].
     *
     * @param datagram
     * the [DatagramPacket] to read from.
     */
    constructor(datagram: DatagramPacket) : this(datagram.content()) {}

    /**
     * Creates a packet from an existing `byte[]`
     *
     * @param data
     * the `byte[]` to read from.
     */
    constructor(data: ByteArray?) : this(Unpooled.copiedBuffer(data)) {}

    /**
     * Creates a packet from an existing packet's buffer.
     *
     * @param packet
     * the packet whose buffer to reference and then read from and
     * write to.
     */
    constructor(packet: Packet) : this(packet.buffer()) {}

    /**
     * Reads data into the specified `byte[]`.
     *
     * @param dest
     * the `byte[]` to read the data into.
     * @return the packet.
     * @throws IndexOutOfBoundsException
     * if there are less readable bytes than the length of
     * `dest`.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun read(dest: ByteArray): Packet {
        for (i in dest.indices) {
            dest[i] = buffer.readByte()
        }
        return this
    }

    /**
     * Reads the specified amount of `byte`s.
     *
     * @param length
     * the amount of `byte`s to read.
     * @return the read `byte`s.
     * @throws IndexOutOfBoundsException
     * if there are less readable bytes than the specified
     * `length`.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun read(length: Int): ByteArray {
        val data = ByteArray(length)
        for (i in data.indices) {
            data[i] = buffer.readByte()
        }
        return data
    }

    /**
     * Skips the specified amount of `byte`s.
     *
     * @param length
     * the amount of `byte`s to skip.
     */
    fun skip(length: Int) {
        buffer.skipBytes(if (length > remaining()) remaining() else length)
    }

    /**
     * Reads a `byte`.
     *
     * @return a `byte`.
     * @throws IndexOutOfBoundsException
     * if there are less than `1` readable byte left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readByte(): Byte {
        return buffer.readByte()
    }

    /**
     * Reads an unsigned `byte`.
     *
     * @return an unsigned `byte`.
     * @throws IndexOutOfBoundsException
     * if there are less than `1` readable byte left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedByte(): Short {
        return (buffer.readUnsignedByte())
    }


    /**
     * Reads a `boolean`.
     *
     * @return `true` if the `byte` read is anything
     * higher than `0`, `false` otherwise.
     * @throws IndexOutOfBoundsException
     * if there are less than `1` readable byte left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readBoolean(): Boolean {
        return readUnsignedByte() > 0x00
    }

    /**
     * Reads a `char`.
     *
     * @return a `char`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readChar(): Char {
        return buffer.readShort().toChar()
    }

    /**
     * Reads a little-endian `char`.
     *
     * @return a little-endian `char`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readCharLE(): Char {
        return buffer.readShortLE().toChar()
    }

    /**
     * Reads a `short`.
     *
     * @return a `short`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readShort(): Short {
        return buffer.readShort()
    }

    /**
     * Reads a little-endian `short`.
     *
     * @return a little-endian `short`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readShortLE(): Short {
        return buffer.readShortLE()
    }

    /**
     * Reads an unsigned `short`.
     *
     * @return an unsigned `short`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */

    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedShort(): UShort {
        return buffer.readUnsignedShort().toUShort()
    }

    /**
     * Reads an unsigned little-endian `short`.
     *
     * @return an unsigned little-endian `short`.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedShortLE(): UShort {
        return buffer.readShortLE().toUShort()
    }

    /**
     * Reads a `triad`.
     *
     * @return a `triad`.
     * @throws IndexOutOfBoundsException
     * if there are less than `3` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readTriad(): Int {
        return buffer.readMedium()
    }

    /**
     * Reads a little-endian `triad`.
     *
     * @return a little-endian `triad`.
     * @throws IndexOutOfBoundsException
     * if there are less than `3` readable bytes left in
     * the packet.
     */
    fun readTriadLE(): Int {
        return buffer.readMediumLE()
    }

    /**
     * Reads an unsigned `triad`.
     *
     * @return an unsigned `triad`.
     * @throws IndexOutOfBoundsException
     * if there are less than `3` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedTriad(): Int {
        return readTriad() and 0xFFFFFF
    }

    /**
     * Reads an unsigned little-endian `triad`.
     *
     * @return an unsigned little-endian `triad`.
     * @throws IndexOutOfBoundsException
     * if there are less than `3` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedTriadLE(): Int {
        return readTriad() and 0xFFFFFF
    }

    /**
     * Reads an `int`.
     *
     * @return an `int`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readInt(): Int {
        return buffer.readInt()
    }

    fun readBlockCoordinates(): Float3  {
        return(Float3(
            readVarInt().toFloat(),
            readUnsignedVarInt().toFloat(),
            readVarInt().toFloat()
        ))
    }

    /**
     * Reads a little-endian `int`.
     *
     * @return a little-endian `int`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readIntLE(): Int {
        return buffer.readIntLE()
    }

    /**
     * Reads an unsigned `int`.
     *
     * @return an unsigned `int`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedInt(): UInt {
        /*
		 * Don't forget the 'L' at the end of 0xFFFFFFFFL. Without it, the
		 * unsigned operation will fail as it will not be ANDing with a long!
		 */
        return buffer.readUnsignedInt().toUInt()
    }

    /**
     * Reads an unsigned little-endian `int`.
     *
     * @return an unsigned little-endian `int`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedIntLE(): Long {
        /*
		 * Don't forget the 'L' at the end of 0xFFFFFFFFL. Without it, the
		 * unsigned operation will fail as it will not be ANDing with a long!
		 */
        return (buffer.readIntLE() and 0xFFFFFFFFL.toInt()).toLong()
    }

    /**
     * Reads a `long`.
     *
     * @return a `long`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readLong(): Long {
        return buffer.readLong()
    }

    /**
     * Reads a little-endian `long`.
     *
     * @return a little-endian `long`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readLongLE(): Long {
        return buffer.readLongLE()
    }

    /**
     * Reads an unsigned `long`.
     *
     * @return an unsigned `long`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedLong(): BigInteger {
        val ulBytes = this.read(java.lang.Long.BYTES)
        return BigInteger(ulBytes)
    }

    /**
     * Reads an unsigned little-endian `long`.
     *
     * @return an unsigned little-endian `long`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUnsignedLongLE(): BigInteger {
        val ulBytesReversed = this.read(java.lang.Long.BYTES)
        val ulBytes = ByteArray(ulBytesReversed.size)
        for (i in ulBytes.indices) {
            ulBytes[i] = ulBytesReversed[ulBytesReversed.size - i - 1]
        }
        return BigInteger(ulBytes)
    }

    /**
     * Reads a `float`.
     *
     * @return a `float`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readFloat(): Float {
        return buffer.readFloat()
    }

    /**
     * Reads a little-endian `float`.
     *
     * @return a little-endian `float`.
     * @throws IndexOutOfBoundsException
     * if there are less than `4` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readFloatLE(): Float {
        return buffer.readFloatLE()
    }

    /**
     * Reads a `double`.
     *
     * @return a `double`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readDouble(): Double {
        return buffer.readDouble()
    }

    /**
     * Reads a little-endian `double`.
     *
     * @return a little-endian `double`.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readDoubleLE(): Double {
        return buffer.readDoubleLE()
    }

    /**
     * Reads a `VarInt`.
     *
     * @return a `VarInt`.
     * @throws IndexOutOfBoundsException
     * if there are not enough bytes to read a `VarInt`
     * or the `VarInt` exceeds the size limit.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.readVarInt
     */
    @Throws(IndexOutOfBoundsException::class, RuntimeException::class)
    fun readVarInt(): Int {
        return try {
            VarInt.readVarInt(inputStream)
        } catch (e: EOFException) {
            throw IndexOutOfBoundsException("VarInt underflow")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Reads an unsigned `VarInt`.
     *
     * @return an unsigned `VarInt`.
     * @throws IndexOutOfBoundsException
     * if there are not enough bytes to read a `VarInt`
     * or the `VarInt` exceeds the size limit.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.readUnsignedVarInt
     */
    @Throws(IndexOutOfBoundsException::class, RuntimeException::class)
    fun readUnsignedVarInt(): Long {
        return try {
            VarInt.readUnsignedVarInt(inputStream)
        } catch (e: EOFException) {
            throw IndexOutOfBoundsException("VarInt underflow")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Reads a `VarLong`.
     *
     * @return a `VarLong`.
     * @throws IndexOutOfBoundsException
     * if there are not enough bytes to read a `VarLong`
     * or the `VarLong` exceeds the size limit.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.readVarLong
     */
    @Throws(IndexOutOfBoundsException::class, RuntimeException::class)
    fun readVarLong(): Long {
        return try {
            VarInt.readVarLong(inputStream)
        } catch (e: EOFException) {
            throw IndexOutOfBoundsException("VarInt underflow")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Reads an unsigned `VarLong`.
     *
     * @return an unsigned `VarLong`.
     * @throws IndexOutOfBoundsException
     * if there are not enough bytes to read a `VarLong`
     * or the `VarLong` exceeds the size limit.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.readUnsignedVarLong
     */
    @Throws(IndexOutOfBoundsException::class, RuntimeException::class)
    fun readUnsignedVarLong(): Long {
        return try {
            VarInt.readUnsignedVarLong(inputStream)
        } catch (e: EOFException) {
            throw IndexOutOfBoundsException("VarLong underflow")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Reads a UTF-8 string with its length prefixed by an unsigned
     * `short`.
     *
     * @return a string.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet to read the length of the string, or if there are
     * less readable bytes than are specified by the length.
     */
    @Throws(IndexOutOfBoundsException::class)
    open fun readString(): String {
       return String(read(readUnsignedVarInt().toInt()))
    }

    /**
     * Reads a UTF-8 string with its length prefixed by a unsigned little
     * -endian `short`.
     *
     * @return a string.
     * @throws IndexOutOfBoundsException
     * if there are less than `2` readable bytes left in
     * the packet to read the length of the string, or if there are
     * less readable bytes than are specified by the length.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readStringLE(): String {
        return readUnsignedShortLE().toString()
    }

    /**
     * Reads an IPv4/IPv6 address.
     *
     * @return an IPv4/IPv6 address.
     * @throws IndexOutOfBoundsException
     * if there are less than `8` readable bytes left in
     * the packet when it is an IPv4 address or `30` when
     * it is an IPv6 address.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found,
     * the family for an IPv6 address was not
     * {@value RakNet#AF_INET6}, a `scope_id` was
     * specified for a global IPv6 address, or the address version
     * is an unknown version.
     */
    @Throws(IndexOutOfBoundsException::class, UnknownHostException::class)
    fun readAddress(): InetSocketAddress {
        val version = readUnsignedByte()
        return if (version.toInt() == RakNet.IPV4) {
            val ipAddress = ByteArray(RakNet.IPV4_ADDRESS_LENGTH)
            for (i in ipAddress.indices) {
                ipAddress[i] = readUnsignedByte().toByte()
            }
            val port = readUnsignedShort()
            InetSocketAddress(InetAddress.getByAddress(ipAddress), port.toInt())
        } else if (version.toInt() == RakNet.IPV6) {
            readShortLE() // Family
            val port = readUnsignedShort()
            readInt() // Flow info
            val ipAddress = ByteArray(RakNet.IPV6_ADDRESS_LENGTH)
            for (i in ipAddress.indices) {
                ipAddress[i] = readByte()
            }
            readInt() // Scope ID
            InetSocketAddress(InetAddress.getByAddress(ipAddress), port.toInt())
        } else {
            throw UnknownHostException("Unknown protocol IPv$version")
        }
    }

    /**
     * Reads a `UUID`.
     *
     * @return a `UUID`.
     * @throws IndexOutOfBoundsException
     * if there are less than `16` readable bytes left in
     * the packet.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun readUUID(): UUID {
        val mostSignificantBits = readLong()
        val leastSignificantBits = readLong()
        return UUID(mostSignificantBits, leastSignificantBits)
    }

    /**
     * Writes the specified `byte`s to the packet.
     *
     * @param data
     * the data to write.
     * @return the packet.
     * @throws NullPointerException
     * if the `data` is `null`.
     */
    @Throws(NullPointerException::class)
    fun write(vararg data: Byte): Packet {
        for (element in data) {
            buffer.writeByte(element.toInt())
        }
        return this
    }

    /**
     * Writes the specified `byte`s to the packet.
     *
     *
     * This method is simply a shorthand for the [.write] method,
     * with all the values being automatically casted back to a
     * `byte` before being sent to the original
     * [.write] method.
     *
     * @param data
     * the data to write.
     * @return the packet.
     * @throws NullPointerException
     * if the `data` is `null`.
     */
    fun write(vararg data: Int): Packet {
        if (data == null) {
            throw NullPointerException("Data cannot be null")
        }
        val bData = ByteArray(data.size)
        for (i in 0 until data.size) {
            bData[i] = data[i].toByte()
        }
        return this.write(*bData)
    }

    fun write(data: Array<Int>): Packet {
        val bData = ByteArray(data.size)
        for (i in 0 until data.size) {
            bData[i] = data[i].toByte()
        }
        return this.write(*bData)
    }
    /**
     * Writes the specified amount of `null` (`0x00`)
     * bytes to the packet.
     *
     * @param length
     * the amount of bytes to write.
     * @return the packet.
     */
    fun pad(length: Int): Packet {
        for (i in 0 until length) {
            buffer.writeByte(0x00)
        }
        return this
    }


    fun writeVector3(vector: Float3) {
        writeFloatLE(vector.x.toDouble())
        writeFloatLE(vector.y.toDouble())
        writeFloatLE(vector.z.toDouble())
    }

    fun readVector3(): Float3 {
        return Float3(readFloatLE(),
            readFloatLE(),
            readFloatLE())
    }

    /**
     * Writes a `byte` to the packet.
     *
     * @param b
     * the `byte`.
     * @return the packet.
     */
    fun writeByte(b: Int): Packet {
        buffer.writeByte(b)
        return this
    }

    fun writeByte(b: Byte): Packet {
        buffer.writeByte(b.toInt())
        return this
    }

    /**
     * Writes an unsigned `byte` to the packet.
     *
     * @param b
     * the unsigned `byte`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `b` is not within the range of
     * `0-255`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedByte(b: Int): Packet {
        require(!(b < 0x00 || b > 0xFF)) { "Value must be in between 0-255" }
        //println(b)
        buffer.writeByte(b and 0xFF)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun writeUnsignedByte(b: UByte): Packet {

        buffer.writeByte(b.toInt())
        return this
    }

    /**
     * Writes a `boolean` to the packet.
     *
     * @param b
     * the `boolean`.
     * @return the packet.
     */
    fun writeBoolean(b: Boolean): Packet {
        buffer.writeByte(if (b) 0x01 else 0x00)
        return this
    }

    /**
     * Writes a `short` to the packet.
     *
     * @param s
     * the `short`.
     * @return the packet.
     */
    fun writeShort(s: Int): Packet {
        buffer.writeShort(s)
        return this
    }

    fun writeShort(s: Short): Packet {
        buffer.writeShort(s.toInt())
        return this
    }

    /**
     * Writes a little-endian `short` to the packet.
     *
     * @param s
     * the `short`.
     * @return the packet.
     */
    fun writeShortLE(s: Int): Packet {
        buffer.writeShortLE(s)
        return this
    }

    /**
     * Writes a unsigned `short` to the packet.
     *
     * @param s
     * the `short`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `s` is not within the range of
     * `0-65535`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedShort(s: Int): Packet {
        require(!(s < 0x0000 || s > 0xFFFF)) { "Value must be in between 0-65535" }
        buffer.writeShort((s.toShort() and 0xFFFF.toShort()).toInt())
        return this
    }

    /**
     * Writes an unsigned little-endian `short` to the packet.
     *
     * @param s
     * the `short`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `s` is not in between `0-65535`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedShortLE(s: Int): Packet {
        require(!(s < 0x0000 || s > 0xFFFF)) { "Value must be in between 0-65535" }
        buffer.writeShortLE((s.toShort() and 0xFFFF.toShort()).toInt())
        return this
    }

    /**
     * Writes a `triad` to the packet.
     *
     * @param t
     * the `triad`.
     * @return the packet.
     */
    fun writeTriad(t: Int): Packet {
        buffer.writeMedium(t)
        return this
    }

    /**
     * Writes a little-endian `triad` to the packet.
     *
     * @param t
     * the `triad`.
     * @return the packet.
     */
    fun writeTriadLE(t: Int): Packet {
        buffer.writeMediumLE(t)
        return this
    }

    /**
     * Writes an unsigned `triad` to the packet.
     *
     * @param t
     * the `triad`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `t` is not in between `0-16777215`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedTriad(t: Int): Packet {
        require(!(t < 0x000000 || t > 0xFFFFFF)) { "Value must be in between 0-16777215" }
        return writeTriad(t and 0xFFFFFF)
    }

    /**
     * Writes an unsigned little-endian `triad` to the packet.
     *
     * @param t
     * the `triad`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `t` is not in between `0-16777215`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedTriadLE(t: Int): Packet {
        require(!(t < 0x000000 || t > 0xFFFFFF)) { "Value must be in between 0-16777215" }
        return writeTriadLE(t and 0xFFFFFF)
    }

    /**
     * Writes an `int` to the packet.
     *
     * @param i
     * the `int`.
     * @return the packet.
     */
    fun writeInt(i: Int): Packet {
        buffer.writeInt(i)
        return this
    }

    /**
     * Writes an unsigned `int` to the packet.
     *
     * @param i
     * the `int`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `i` is not in between `0-4294967295`
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedInt(i: Long): Packet {
        require(!(i < 0x00000000 || i > 0xFFFFFFFFL)) { "Value must be in between 0-4294967295" }
        buffer.writeInt(i.toInt() and -0x1)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun writeUnsignedInt(i: UInt): Packet {
        buffer.writeInt(i.toInt() and -0x1)
        return this
    }


    /**
     * Writes a little-endian `int` to the packet.
     *
     * @param i
     * the `int`.
     * @return the packet.
     */
    fun writeIntLE(i: Int): Packet {
        buffer.writeIntLE(i)
        return this
    }

    /**
     * Writes an unsigned little-endian `int` to the packet.
     *
     * @param i
     * the `int`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `i` is not in between
     * `0-4294967295`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedIntLE(i: Long): Packet {
        require(!(i < 0x00000000 || i > 0xFFFFFFFFL)) { "Value must be in between 0-4294967295" }
        buffer.writeIntLE(i.toInt() and -0x1)
        return this
    }

    /**
     * Writes a `long` to the packet.
     *
     * @param l
     * the `long`.
     * @return the packet.
     */
    fun writeLong(l: Long): Packet {
        buffer.writeLong(l)
        return this
    }

    /**
     * Writes a little-endian `long` to the packet.
     *
     * @param l
     * the `long`.
     * @return the packet.
     */
    fun writeLongLE(l: Long): Packet {
        buffer.writeLongLE(l)
        return this
    }

    /**
     * Writes an unsigned `long` to the packet.
     *
     * @param bi
     * the `long`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `bi` is bigger than {@value Long#BYTES} bytes
     * or is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedLong(bi: BigInteger): Packet {
        val ulBytes = bi.toByteArray()
        require(ulBytes.size <= java.lang.Long.BYTES) { "Value is too big to fit into a long" }
        require(bi.toLong() >= 0) { "Value cannot be negative" }
        for (i in 0 until java.lang.Long.BYTES) {
            writeByte((if (i < ulBytes.size) ulBytes[i] else 0x00).toInt())
        }
        return this
    }

    /**
     * Writes an unsigned `long` to the packet.
     *
     * @param l
     * the `long`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `l` is less than `0`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedLong(l: Long): Packet {
        return this.writeUnsignedLong(BigInteger(java.lang.Long.toString(l)))
    }

    /**
     * Writes an unsigned little-endian `long` to the packet.
     *
     * @param bi
     * the `long`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if the size of the `bi` is bigger than
     * {@value Long#BYTES} bytes or is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedLongLE(bi: BigInteger): Packet {
        val ulBytes = bi.toByteArray()
        require(ulBytes.size <= java.lang.Long.BYTES) { "Value is too big to fit into a long" }
        require(bi.toLong() >= 0) { "Value cannot be negative" }
        for (i in java.lang.Long.BYTES - 1 downTo 0) {
            writeByte((if (i < ulBytes.size) ulBytes[i] else 0x00).toInt())
        }
        return this
    }

    /**
     * Writes an unsigned little-endian `long` to the packet.
     *
     * @param l
     * the `long`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `l` is less than `0`.
     */
    @Throws(IllegalArgumentException::class)
    fun writeUnsignedLongLE(l: Long): Packet {
        return this.writeUnsignedLongLE(BigInteger(java.lang.Long.toString(l)))
    }

    /**
     * Writes a `float` to the packet.
     *
     * @param f
     * the `float`.
     * @return the packet.
     */
    @Deprecated("use writeFloat('float')")
    fun writeFloat(f: Double): Packet {
        buffer.writeFloat(f.toFloat())
        return this
    }


    fun writeFloat(f: Float): Packet {
        buffer.writeFloat(f)
        return this
    }
    /**
     * Writes a little-endian `float` to the packet.
     *
     * @param f
     * the `float`.
     * @return the packet.
     */
    fun writeFloatLE(f: Double): Packet {
        buffer.writeFloatLE(f.toFloat())
        return this
    }

    fun writeFloatLE(f: Float): Packet {
        buffer.writeFloatLE(f)
        return this
    }
    /**
     * Writes a `double` to the packet.
     *
     * @param d
     * the `double`.
     * @return the packet.
     */
    fun writeDouble(d: Double): Packet {
        buffer.writeDouble(d)
        return this
    }

    /**
     * Writes a `double` to the packet.
     *
     * @param d
     * the `double`.
     * @return the packet.
     */
    fun writeDoubleLE(d: Double): Packet {
        buffer.writeDoubleLE(d)
        return this
    }

    /**
     * Writes a `VarInt` to the packet.
     *
     * @param i
     * the `VarInt`.
     * @return the packet.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.writeVarInt
     */
    @Throws(RuntimeException::class)
    fun writeVarInt(i: Int): Packet {
        try {
            VarInt.writeVarInt(i, outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return this
    }

    /**
     * Writes a unsigned `VarInt` to the packet.
     *
     * @param i
     * the `VarInt`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `i` is not within the range of
     * `0-4294967295`.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.writeUnsignedVarInt
     */
    @Throws(RuntimeException::class)
    fun writeUnsignedVarInt(i: Int): Packet {
        require(!(i < 0x00000000 || i > 0xFFFFFFFFL)) { "Value must be in between 0-4294967295" }
        try {
            VarInt.writeUnsignedVarInt(i, outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return this
    }

    

    /**
     * Writes a `VarLong` to the packet.
     *
     * @param l
     * the `VarLong`.
     * @return the packet.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.writeVarLong
     */
    @Throws(RuntimeException::class)
    fun writeVarLong(l: Long): Packet {
        try {
            VarInt.writeVarLong(l, outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return this
    }

    /**
     * Writes a unsigned `VarLong` to the packet.
     *
     * @param l
     * the `VarLong`.
     * @return the packet.
     * @throws IllegalArgumentException
     * if `i` is not within the range of
     * `0-18446744073709551615L`.
     * @throws RuntimeException
     * if an I/O error occurs despite the fact it should never
     * happen.
     * @see VarInt.writeUnsignedVarLong
     */
    @Throws(RuntimeException::class)
    fun writeUnsignedVarLong(l: Long): Packet {
        //require(!(l < 0x0000000000000000 || l > -0x1L)) { "Value must be in between 0-18446744073709551615L" }
        try {
            VarInt.writeUnsignedVarLong(l, outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return this
    }

/*
    fun writeUnsignedVarLong(l: Long): Packet {
        //require(!(l < 0x0000000000000000 || l > -0x1L)) { "Value must be in between 0-18446744073709551615L" }
        try {
            VarInt.writeUnsignedVarLong(l, outputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return this
    }
   */

    /**
     * Writes a UTF-8 string prefixed by an unsigned `short` to the
     * packet.
     *
     * @param s
     * the string.
     * @return the packet.
     * @throws NullPointerException
     * if `s` is `null`.
     */
    @Throws(NullPointerException::class)
    open fun writeString(s: String?): Packet {
        if (s == null) {
            throw NullPointerException("String cannot be null")
        }
        val data = s.toByteArray()
        writeUnsignedShort(data.size)
        this.write(*data)
        return this
    }

    /**
     * Writes a UTF-8 string prefixed by a little-endian unsigned
     * `short` to the packet.
     *
     * @param s
     * the strin.
     * @return the packet.
     * @throws NullPointerException
     * if `s` is `null`.
     */
    @Throws(NullPointerException::class)
    fun writeStringLE(s: String?): Packet {
        if (s == null) {
            throw NullPointerException("String cannot be null")
        }
        val data = s.toByteArray()
        writeUnsignedShortLE(data.size)
        this.write(*data)
        return this
    }

    /**
     * Writes an IPv4/IPv6 address to the packet.
     *
     * @param address
     * the address.
     * @return the packet.
     * @throws NullPointerException
     * if the `address` or IP address are
     * `null`.
     * @throws UnknownHostException
     * if no IP address for the `host` could be found, if
     * a `scope_id` was specified for a global IPv6
     * address, or the length of the address is not either
     * {@value RakNet#IPV4_ADDRESS_LENGTH} or
     * {@value RakNet#IPV6_ADDRESS_LENGTH} `byte`s.
     */
    @Throws(NullPointerException::class, UnknownHostException::class)
    fun writeAddress(address: InetSocketAddress?): Packet {
        if (address == null) {
            throw NullPointerException("Address cannot be null")
        } else if (address.address == null) {
            throw NullPointerException("IP address cannot be null")
        }
        val ipAddress = address.address.address
        val version = RakNet.getAddressVersion(address)
        if (version == RakNet.IPV4) {
            writeUnsignedByte(RakNet.IPV4)
            for (i in ipAddress.indices) {
                writeUnsignedByte(ipAddress[i].toUByte())
            }
            writeUnsignedShort(address.port)
        } else if (version == RakNet.IPV6) {
            writeUnsignedByte(RakNet.IPV6)
            writeShortLE(RakNet.AF_INET6)
            writeShort(address.port)
            writeInt(0x00) // Flow info
            this.write(*ipAddress)
            writeInt(0x00) // Scope ID
        } else {
            throw UnknownHostException(
                "Unknown protocol for address with length of " + ipAddress.size + " bytes"
            )
        }
        return this
    }

    /**
     * Writes an IPv4 address to the packet.
     *
     * @param host
     * the IP address.
     * @param port
     * the port.
     * @return the packet.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws IllegalArgumentException
     * if the port is not in between `0-65535`.
     * @throws UnknownHostException
     * if no IP address for the `host` could not be
     * found, or if a `scope_id` was specified for a
     * global IPv6 address.
     */
    @Throws(NullPointerException::class, IllegalArgumentException::class, UnknownHostException::class)
    fun writeAddress(host: InetAddress?, port: Int): Packet {
        if (host == null) {
            throw NullPointerException("Host cannot be null")
        } else require(!(port < 0x0000 || port > 0xFFFF)) { "Port must be in between 0-65535" }
        return this.writeAddress(InetSocketAddress(host, port))
    }

    /**
     * Writes an IPv4 address to the packet (IPv6 is not yet supported).
     *
     * @param host
     * the IP address.
     * @param port
     * the port.
     * @return the packet.
     * @throws NullPointerException
     * if the `host` is `null`.
     * @throws IllegalArgumentException
     * if the port is not in between `0-65535`.
     * @throws UnknownHostException
     * if no IP address for the `host` could not be
     * found, or if a `scope_id` was specified for a
     * global IPv6 address.
     */
    @Throws(NullPointerException::class, IllegalArgumentException::class, UnknownHostException::class)
    fun writeAddress(host: String?, port: Int): Packet {
        if (host == null) {
            throw NullPointerException("Host cannot be null")
        } else require(!(port < 0x0000 || port > 0xFFFF)) { "Port must be in between 0-65535" }
        return this.writeAddress(InetAddress.getByName(host), port)
    }

    /**
     * Writes a `UUID` to the packet.
     *
     * @param uuid
     * the `UUID`.
     * @return the packet.
     * @throws NullPointerException
     * if the `uuid` is `null`.
     */
    @Throws(NullPointerException::class)
    fun writeUUID(uuid: UUID?): Packet {
        if (uuid == null) {
            throw NullPointerException("UUID cannot be null")
        }
        writeLong(uuid.mostSignificantBits)
        writeLong(uuid.leastSignificantBits)
        return this
    }

    fun writeItem(item: Item?, instance: Boolean = false) {
        //varInt runtimeId
        if(item != null) {
            writeVarInt(item.runtimeId) //runtimeId
            writeShortLE(item.count) //count
            writeUnsignedVarInt(if (item.iDurability == -1) 0 else item.iDurability) // damage
            if(!instance) {
                writeBoolean(true) //instance
                writeVarInt(0) //instance
            }
            writeVarInt(PaletteGlobal.getRuntimeIdFromName(item.name)) //blockid
            //writeVarInt(0)
            //last payload size todo
            writeUnsignedVarInt(10)
            writeShortLE(0) // no meta
            writeInt(0) //can placeon
            writeInt(0) // can place on
        } else {
            writeByte(0)
//            writeVarInt(163)
//            writeShortLE(0)
//            writeInt(0)
//            writeInt(0)
        }
        //lShort count

        //uVarInt damage
        //varInt blockruntimeId or 0
        //shortLE of 0
        //canplace of Int of 0
        //canDestroy of int of 0
    }

    fun readItem(): Item {
        return TODO()
    }


    /**
     * Returns the packet as a `byte[]`.
     *
     * @return the packet as a `byte[]`, `null` if the
     * buffer being used within the packet is a direct buffer.
     */
    fun array(): ByteArray? {
        return if (buffer.isDirect) {
            null
        } else Arrays.copyOfRange(buffer.array(), 0, buffer.writerIndex())
    }

    /**
     * Returns the size of the packet in `byte`s.
     *
     *
     * This is to be used only for packets that are being written to. To get the
     * amount of bytes that are still readable, use the [.remaining]
     * method.
     *
     * @return the size of the packet in `byte`s.
     */
    fun size(): Int {
        return buffer.writerIndex()
    }

    /**
     * Returns the packet buffer.
     *
     *
     * This method will not increase the buffer's reference count via the
     * [ByteBuf.retain] method. It is up to the original packet creator
     * to release this packet's buffer.
     *
     *
     * Packet buffers are released when they are actually sent over the internal
     * pipelines of either a server or a client. As a result, one does not
     * normally need to worry about releasing a packet buffer so long as they
     * plan to eventually send the packet.
     *
     * @return the packet buffer.
     */
    fun buffer(): ByteBuf {
        return buffer
    }

    /**
     * Returns a copy of the packet buffer.
     *
     * @return a copy of the packet buffer.
     */
    fun copy(): ByteBuf {
        return buffer.copy()
    }

    /**
     * Releases the packet's buffer.
     *
     * @return `true` if and only if the reference count became
     * `0` and this object has been deallocated,
     * `false` otherwise.
     */
    fun release(): Boolean {
        return buffer.release()
    }

    /**
     * Returns how many readable `byte`s are left in the packet's
     * buffer.
     *
     *
     * This is to only be used for packets that are being read from. To get the
     * amount of bytes that have been written to the packet, use the
     * [.size] method.
     *
     * @return how many readable `byte`s are left in the packet's
     * buffer.
     */
    fun remaining(): Int {
        return buffer.readableBytes()
    }

    /**
     * Updates the buffer.
     *
     * @param buffer
     * the buffer to read from and write to, a `null`
     * value will have a new buffer be used instead.
     * @return the packet.
     */
    fun setBuffer(buffer: ByteBuf?): Packet {
        this.buffer = buffer ?: Unpooled.buffer()
        return this
    }

    /**
     * Updates the buffer.
     *
     * @param datagram
     * the [DatagramPacket] whose buffer to read from and write
     * to.
     * @return the packet.
     * @throws NullPointerException
     * if the `datagram` packet is `null`.
     */
    @Throws(NullPointerException::class)
    fun setBuffer(datagram: DatagramPacket?): Packet {
        if (datagram == null) {
            throw NullPointerException("Datagram packet cannot be null")
        }
        return this.setBuffer(datagram.content())
    }

    /**
     * Updates the buffer.
     *
     * @param data
     * the `byte[]` to create the new buffer from.
     * @return the packet.
     * @throws NullPointerException
     * if the `data` is `null`.
     */
    @Throws(NullPointerException::class)
    fun setBuffer(data: ByteArray?): Packet {
        if (data == null) {
            throw NullPointerException("Data cannot be null")
        }
        return this.setBuffer(Unpooled.copiedBuffer(data))
    }

    /**
     * Updates the buffer.
     *
     * @param packet
     * the packet whose buffer to copy to read from and write to.
     * @return the packet.
     * @throws NullPointerException
     * if the `packet` is `null`.
     */
    @Throws(NullPointerException::class)
    fun setBuffer(packet: Packet?): Packet {
        if (packet == null) {
            throw NullPointerException("Packet cannot be null")
        }
        return this.setBuffer(packet.copy())
    }

    /**
     * Flips the packet.
     *
     *
     * Flipping the packet will cause the current internal buffer to be released
     * with the a new buffer taking it's place. The newly created buffer will
     * retain the reference count of the original buffer before it was
     * de-allocated.
     *
     * @return the packet.
     */
    open fun flip(): Packet? {
        val data = buffer.array()
        val increment = buffer.refCnt()
        buffer.release(increment) // No longer needed
        buffer = Unpooled.copiedBuffer(data)
        buffer.retain(increment)
        return this
    }

    /**
     * Clears the packet's buffer.
     *
     * @return the packet.
     */
    fun clear(): Packet {
        buffer.clear()
        return this
    }

//    fun verbose(): String {
//        return buffer.array().toTypedArray().map { byte ->
//            byte.toString(16)
//        }
//    }

    /**
     * Creates a packet using the specified [ByteBuf]
     *
     * @param buffer
     * the [ByteBuf] to read from and write to, a
     * `null` value will have a new buffer be used
     * instead.
     * @throws IllegalArgumentException
     * if the `buffer` is an [EmptyByteBuf].
     */
    /**
     * Creates an empty packet.
     */
    init {
        this.buffer = buffer ?: Unpooled.buffer()
        inputStream = PacketDataInputStream(this)
        outputStream = PacketDataOutputStream(this)
    }
}