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
package bedrockDragon.network.raknet.stream

import bedrockDragon.network.raknet.Packet
import kotlin.Throws
import java.io.*
import java.lang.IndexOutOfBoundsException

/**
 * Used as a way for a [Packet] to be used where an [InputStream] is
 * required.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.1.0
 * @see Packet.getInputStream
 */

@OptIn(ExperimentalUnsignedTypes::class)
class PacketDataInputStream(private val packet: Packet) : InputStream(), DataInput {
    private val dataIn: DataInputStream
    @Throws(IOException::class, EOFException::class)
    override fun read(): Int {
        return if (packet.remaining() > 0) {
            packet.readUnsignedByte().toInt()
        } else EOF.toInt()
    }

    @Throws(IOException::class, EOFException::class)
    override fun readFully(b: ByteArray) {
        for (i in b.indices) {
            b[i] = packet.readByte()
            if (b[i] == EOF) {
                throw EOFException()
            }
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readFully(b: ByteArray, off: Int, len: Int) {
        for (i in off until len) {
            b[i] = packet.readByte()
            if (b[i] == EOF) {
                throw EOFException()
            }
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun skipBytes(n: Int): Int {
        var skipped = 0
        while (skipped < n && packet.remaining() > 0) {
            packet.readByte()
            skipped++
        }
        return skipped
    }

    @Throws(IOException::class, EOFException::class)
    override fun readBoolean(): Boolean {
        return try {
            packet.readBoolean()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readByte(): Byte {
        return try {
            packet.readByte()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readUnsignedByte(): Int {
        return try {
            packet.readUnsignedByte().toInt()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readShort(): Short {
        return try {
            packet.readShort()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }


    @Throws(IOException::class, EOFException::class)
    override fun readUnsignedShort(): Int {
        return try {
            packet.readUnsignedShort().toInt()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readChar(): Char {
        return try {
            packet.readUnsignedShort() as Char
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readInt(): Int {
        return try {
            packet.readInt()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readLong(): Long {
        return try {
            packet.readLong()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readFloat(): Float {
        return try {
            packet.readFloat()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    @Throws(IOException::class, EOFException::class)
    override fun readDouble(): Double {
        return try {
            packet.readDouble()
        } catch (e: IndexOutOfBoundsException) {
            throw EOFException()
        }
    }

    /**
     * {@inheritDoc}
     *
     *
     * This method is implemented via a [DataInputStream] which refers
     * back to this original stream to execute the [.readLine] method.
     */
    @Deprecated("")
    @Throws(IOException::class)
    override fun readLine(): String {
        return dataIn.readLine()
    }

    /**
     * {@inheritDoc}
     *
     *
     * This method is implemented via a [DataInputStream] which refers
     * back to this original stream to execute the [.readLine] method.
     */
    @Throws(IOException::class, EOFException::class)
    override fun readUTF(): String {
        return dataIn.readUTF()
    }

    companion object {
        /**
         * Signals that the end of a file has been reached.
         */
        private const val EOF: Byte = -1
    }

    /**
     * Creates a packet input stream to read data from the specified underlying
     * packet.
     *
     * @param packet
     * the underlying packet.
     */
    init {
        dataIn = DataInputStream(this)
    }
}