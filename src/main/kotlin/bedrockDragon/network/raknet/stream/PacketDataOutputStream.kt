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
import java.io.DataOutput
import java.io.DataOutputStream
import kotlin.Throws
import java.io.IOException
import java.io.OutputStream

/**
 * Used as a way for a [Packet] to be used where an [OutputStream]
 * is required.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.1.0
 * @see Packet.getOutputStream
 */
class PacketDataOutputStream(private val packet: Packet) : OutputStream(), DataOutput {
    private val dataOut: DataOutputStream
    @Throws(IOException::class)
    override fun write(b: Int) {
        packet.writeByte(b)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray) {
        packet.writeB(*b)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        for (i in off until len) {
            packet.writeByte(b[i].toInt())
        }
    }

    @Throws(IOException::class)
    override fun writeBoolean(v: Boolean) {
        packet.writeBoolean(v)
    }

    @Throws(IOException::class)
    override fun writeByte(v: Int) {
        packet.writeByte(v)
    }

    @Throws(IOException::class)
    override fun writeShort(v: Int) {
        packet.writeShort(v)
    }

    @Throws(IOException::class)
    override fun writeChar(v: Int) {
        packet.writeUnsignedShort(v)
    }

    @Throws(IOException::class)
    override fun writeInt(v: Int) {
        packet.writeInt(v)
    }

    @Throws(IOException::class)
    override fun writeLong(v: Long) {
        packet.writeLong(v)
    }

    @Throws(IOException::class)
    override fun writeFloat(v: Float) {
        packet.writeFloat(v.toDouble())
    }

    @Throws(IOException::class)
    override fun writeDouble(v: Double) {
        packet.writeDouble(v)
    }

    @Throws(IOException::class)
    override fun writeBytes(s: String) {
        packet.writeB(*s.toByteArray())
    }

    @Throws(IOException::class)
    override fun writeChars(s: String) {
        for (c in s.toCharArray()) {
            packet.writeUnsignedShort(c.toInt())
        }
    }

    /**
     * {@inheritDoc}
     *
     *
     * This method is implemented via a [DataOutputStream] which refers
     * back to this original stream to execute the [.writeUTF]
     * method.
     */
    @Throws(IOException::class)
    override fun writeUTF(s: String) {
        dataOut.writeUTF(s)
    }

    /**
     * Creates a new packet output stream to write data to the specified
     * underlying packet.
     *
     * @param packet
     * the underlying packet.
     */
    init {
        dataOut = DataOutputStream(this)
    }
}