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
package bedrockDragon.network.raknet.protocol.message.acknowledge

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record.Companion.simplify
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.handler.PacketConstants
import java.util.ArrayList

/**
 * An `ACK` packet.
 *
 *
 * This packet is sent when a packet that requires an acknowledgement receipt is
 * received. This enables for servers and clients to know when the other side
 * has received their message, which can be crucial during the login process.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 * @see java.lang.Record
 */

@OptIn(ExperimentalUnsignedTypes::class)
open class AcknowledgedPacket : RakNetPacket {
    /**
     * The records containing the sequence IDs.
     */
    lateinit var records: Array<Record>

    /**
     * Creates an `ACK` packet to be encoded.
     *
     * @param acknowledge
     * `true` if the records inside the packet are
     * acknowledged, `false` if the records are not
     * acknowledged.
     * @see .encode
     */

    /**
     * Creates an `ACK` packet to be encoded.
     *
     * @see .encode
     */
    protected constructor(id : Int) : super(id) {}
    constructor() : super(0xC0)
    /**
     * Creates an `ACK` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    /**
     * Returns whether or not the records inside the packet are acknowledged.
     *
     * @return `true` if the records inside the packet are
     * acknowledged, `false` if the records are not
     * acknowledged.
     */
    val isAcknowledgement: Boolean
        get() = id.toInt() == PacketConstants.ACK

    /**
     * {@inheritDoc}
     *
     *
     * Before encoding, all records will be condensed. This means that all
     * records that can be converted to ranged records will be converted to
     * ranged records, making them use less memory. The `records`
     * field will be updated with these condensed records.
     */
    override fun encode() {
        //this.records = Record.condense(records);
        writeUnsignedShort(records.size)
        for (record in records) {
            writeUnsignedByte(if (record.isRanged) RANGED else UNRANGED)
            writeTriadLE(record.getIndex())
            if (record.isRanged) {
                writeTriadLE(record.getEndIndex())
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     *
     * After decoding is finished, all records will be expanded. This means that
     * all ranged records will be converted to single records, making it easier
     * to cycle through them. The `records` field will be updated
     * with these expanded records.
     */
    override fun decode() {
        val records = ArrayList<Record>()
        val size = readUnsignedShort().toInt()
        for (i in 0 until size) {
            val ranged = readUnsignedByte().toInt() == RANGED
            if (!ranged) {
                records.add(Record(readTriadLE()))
            } else {
                records.add(Record(readTriadLE(), readTriadLE()))
            }
        }
        this.records = simplify(records)
    }

    companion object {
        /**
         * The record is unranged.
         */
        const val RANGED = 0x00

        /**
         * The record is ranged.
         */
        const val UNRANGED = 0x01
    }
}