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

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.experimental.or

/**
 * Utilities for reading and writing both `VarInt`s and
 * `VarLong`s.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.11.4
 */
object VarInt {
    /**
     * The maximum amount of bits a `VarInt` can be.
     */
    private const val VARINT_MAX_SIZE = 35

    /**
     * The maximum amount of bits a `VarLong` can be.
     */
    private const val VARLONG_MAX_SIZE = 70

    /**
     * Reads a `VarInt` from the specified [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @param max
     * the maximum amount of bits the `VarInt` can be.
     * @return a `VarInt`.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IllegalArgumentException
     * if the `max` bits is less than or equal to
     * `0` or is greater than {@value #VARLONG_MAX_SIZE}.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds the `max`
     * amount of bits.
     */
    @Throws(IOException::class)
    fun read(`in`: InputStream?, max: Int): Long {
        if (`in` == null) {
            throw NullPointerException("Input stream cannot be null")
        } else require(max > 0) { "Max bits must be greater than 0" }
            require(max <= VARLONG_MAX_SIZE) { "Max bits can be no greater than " + VARLONG_MAX_SIZE }
        var result = 0
        var shift = 0
        var bits: Int
        do {
            if (shift >= max) {
                throw IndexOutOfBoundsException("VarInt overflow")
            }
            bits = `in`.read()
            if (bits < 0) {
                throw EOFException("VarInt underflow")
            }
            result = result or (bits and 0x7F shl shift)
            shift += 7
        } while (bits and 0x80 > 0)
        return result.toLong()
    }

    /**
     * Writes a `VarInt` to the given [OutputStream].
     *
     * @param l
     * the value to write.
     * @param out
     * the output stream to write to.
     * @param max
     * the maximum amount of bits the `VarInt` can be.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IllegalArgumentException
     * if the `max` bits is less than or equal to
     * `0` or is greater than {@value #VARLONG_MAX_SIZE}.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds the `max`
     * amount of bits.
     */
    @Throws(
        NullPointerException::class,
        IllegalArgumentException::class,
        IOException::class,
        IndexOutOfBoundsException::class
    )
    fun write(l: Long, out: OutputStream?, max: Int) {
        if (out == null) {
            throw NullPointerException("Output stream cannot be null")
        } else require(max > 0) { "Max bits must be greater than 0" }
            require(max <= VARLONG_MAX_SIZE) { "Max bits can be no greater than " + VARLONG_MAX_SIZE }
        var more = true
        var shift = 0
        while (more == true) {
            if (max != VARLONG_MAX_SIZE && shift >= max) {
                throw IndexOutOfBoundsException("VarInt overflow")
            }
            val bits = (l ushr shift) and 0x7F
            shift += 7
            if (shift >= VARLONG_MAX_SIZE || l ushr shift == 0L) {
                more = false
            }
            out.write((bits or if (more == true) 0x80 else 0x00).toInt())
        }
    }

    /**
     * Writes a `VarInt` to the given [OutputStream].
     *
     * @param i
     * the value to write.
     * @param out
     * the output stream to write to.
     * @param max
     * the maximum amount of bits the `VarInt` can be.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IllegalArgumentException
     * if the `max` bits is less than or equal to
     * `0` or is greater than {@value #VARINT_MAX_SIZE}.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds the `max`
     * amount of bits.
     */
    @Throws(
        NullPointerException::class,
        IllegalArgumentException::class,
        IOException::class,
        IndexOutOfBoundsException::class
    )
    fun write(i: Int, out: OutputStream?, max: Int) {
        if (out == null) {
            throw NullPointerException("Output stream cannot be null")
        } else require(max > 0) { "Max bits must be greater than 0" }
            require(max <= VARINT_MAX_SIZE) { "Max bits can be no greater than " + VARINT_MAX_SIZE }
        var more = true
        var shift = 0
        while (more == true) {
            if (max != VARINT_MAX_SIZE && shift >= max) {
                throw IndexOutOfBoundsException("VarInt overflow")
            }
            val bits = i ushr shift and 0x7F
            shift += 7
            if (shift >= VARINT_MAX_SIZE || i ushr shift == 0) {
                more = false
            }
            out.write(bits or if (more == true) 0x80 else 0x00)
        }
    }

    /**
     * Reads a `VarInt` from the specified [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @return a `VarInt`.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds {@value #VARINT_MAX_SIZE}
     * bits.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    fun readVarInt(`in`: InputStream?): Int {
        return decodeZigZag32(read(`in`, VARINT_MAX_SIZE).toInt())
    }

    /**
     * Writes a `VarInt` to the given [OutputStream].
     *
     * @param i
     * the value to write.
     * @param out
     * the output stream to write to.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds {@value #VARINT_MAX_SIZE}
     * bits.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    fun writeVarInt(i: Int, out: OutputStream?) {
        writeUnsignedVarInt(i, out, true)
    }

    /**
     * Reads a `VarLong` from the specified [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @return a `VarLong`.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarLong` exceeds
     * {@value #VARLONG_MAX_SIZE} bits.
     */
    @Throws(IOException::class)
    fun readVarLong(`in`: InputStream?): Long {
        return read(`in`, VARLONG_MAX_SIZE)
    }

    /**
     * Writes a `VarLong` to the given [OutputStream].
     *
     * @param l
     * the value to write.
     * @param out
     * the output stream to write to.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarLong` exceeds
     * {@value #VARLONG_MAX_SIZE} bits.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    fun writeVarLong(l: Long, out: OutputStream?) {
        writeUnsignedVarLong(l, out, true)
    }

    /**
     * Reads an unsigned `VarInt` from the specified
     * [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @param max
     * the maximum amount of bits the `VarInt` can be.
     * @return an unsigned `VarInt`.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IllegalArgumentException
     * if the `max` bits is less than or equal to
     * `0` or is greater than {@value #VARLONG_MAX_SIZE}.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds the `max`
     * amount of bits.
     */
    @Throws(
        NullPointerException::class,
        IllegalArgumentException::class,
        IOException::class,
        IndexOutOfBoundsException::class
    )
    fun readUnsigned(`in`: InputStream?, max: Int): Long {
        if (`in` == null) {
            throw NullPointerException("Input stream cannot be null")
        } else require(max > 0) { "Max bits must be greater than 0" }
            require(max <= VARLONG_MAX_SIZE) { "Max bits can be no greater than " + VARLONG_MAX_SIZE }
        var result: Long = 0
        var shift = 0
        var bits: Int
        do {
            if (shift >= max) {
                throw IndexOutOfBoundsException("VarInt overflow")
            }
            bits = `in`.read()
            if (bits < 0) {
                throw EOFException("VarInt underflow")
            }
            result = result or ((bits and 0x7F).toLong() shl shift)
            shift += 7
        } while (bits and 0x80 > 0)
        return result
    }

    /**
     * Writes an unsigned `VarInt` to the given [OutputStream].
     *
     * @param l
     * the value to write.
     * @param out
     * the output stream to write to.
     * @param max
     * the maximum amount of bits the `VarInt` can be.
     * @throws IllegalArgumentException
     * if the value is negative or the `max` bits is less
     * than or equal to `0` or is greater than
     * {@value #VARLONG_MAX_SIZE}.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds the `max`
     * amount of bits.
     */

    fun writeUnsigned(l: Long, out: OutputStream?, max: Int) {
        if (out == null) {
            throw NullPointerException("Output stream cannot be null")
        } else require(max >= 0) { "Max bits must be greater than 0" }
        require(max <= VARLONG_MAX_SIZE) { "Max bits can be no greater than " + VARLONG_MAX_SIZE }
        var value = l
        do {
            var temp = (value and 127).toByte()
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value = value ushr 7
            if (value != 0L) {
                temp = temp or 128.toByte()
            }
            out.write(ByteArray(1) {temp})
        } while (value != 0L)

    }

    /**
     * Reads an unsigned `VarInt` from the specified
     * [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @return an unsigned `VarInt`.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if the `VarInt` exceeds {@value #VARINT_MAX_SIZE}
     * bits.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    fun readUnsignedVarInt(`in`: InputStream?): Long {
        return readUnsigned(`in`, VARINT_MAX_SIZE)
    }

    /**
     * Writes an unsigned `VarInt` to the given [OutputStream].
     *
     * @param i
     * the value to write.
     * @param out
     * the output stream to write to.
     * @throws IllegalArgumentException
     * if the value is negative.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if more than {@value #VARINT_MAX_SIZE} bits are written.
     */
    @Throws(
        IllegalArgumentException::class,
        NullPointerException::class,
        IOException::class,
        IndexOutOfBoundsException::class
    )
    fun writeUnsignedVarInt(i: Int, out: OutputStream?) {
        writeUnsignedVarInt(i, out, false)
    }

    /**
     * Reads an unsigned `VarLong` from the specified
     * [InputStream].
     *
     * @param in
     * the input stream to read from.
     * @return an unsigned `VarLong`.
     * @throws IllegalArgumentException
     * if the `value` is negative.
     * @throws NullPointerException
     * if the `in` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if more than {@value #VARLONG_MAX_SIZE} bits are read.
     */
    @Throws(NullPointerException::class, IOException::class, IndexOutOfBoundsException::class)
    fun readUnsignedVarLong(`in`: InputStream?): Long {
        return readUnsigned(`in`, VARLONG_MAX_SIZE)
    }

    /**
     * Writes an unsigned `VarLong` to the given
     * [OutputStream].
     *
     * @param l
     * the value to write.
     * @param out
     * the output stream to write to.
     * @throws IllegalArgumentException
     * if the value is negative.
     * @throws NullPointerException
     * if the `out` stream is `null`.
     * @throws IOException
     * if an I/O error occurs.
     * @throws IndexOutOfBoundsException
     * if more than {@value #VARLONG_MAX_SIZE} bits are written.
     */
    @Throws(
        IllegalArgumentException::class,
        NullPointerException::class,
        IOException::class,
        IndexOutOfBoundsException::class
    )
    fun writeUnsignedVarLong(l: Long, out: OutputStream?) {
            writeUnsignedVarLong(l, out, false)
    }

    fun writeUnsignedVarLong(l: Long, out: OutputStream?, zigzag: Boolean) {
        if(zigzag) {
            writeUnsigned(encodeZigZag64(l), out, VARLONG_MAX_SIZE)

        } else {
            writeUnsigned(l, out, VARLONG_MAX_SIZE)
        }
    }



    fun writeUnsignedVarInt(l: Int, out: OutputStream?, zigzag: Boolean) {

        if(zigzag) {
            writeUnsigned(encodeZigZag32(l), out, VARINT_MAX_SIZE)

        } else {
            writeUnsigned(l.toLong(), out, VARINT_MAX_SIZE)
        }
    }
    private fun decodeZigZag32(v: Int): Int {
            return (v shr 1) xor -(v and 1)
    }

    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */

    private fun encodeZigZag32(v: Int): Long {

        // Note:  the right-shift must be arithmetic
        return (v shl 1 xor (v shr 31) and 0xFFFFFFFFL.toInt()).toLong()
    }

    private fun encodeZigZag64(v: Long): Long {
        return v shl 1 xor (v shr 63)
    }
}