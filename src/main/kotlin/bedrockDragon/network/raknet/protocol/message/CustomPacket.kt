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
package bedrockDragon.network.raknet.protocol.message

import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import java.lang.IllegalArgumentException
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record
import java.util.ArrayList

/**
 * A `CUSTOM_0`, `CUSTOM_1`, `CUSTOM_2`,
 * `CUSTOM_3`, `CUSTOM_4`, `CUSTOM_5`,
 * `CUSTOM_6`, `CUSTOM_7`, `CUSTOM_8`,
 * `CUSTOM_9`, `CUSTOM_A`, `CUSTOM_B`,
 * `CUSTOM_C`, `CUSTOM_D`, `CUSTOM_E`, or
 * `CUSTOM_F` packet.
 *
 *
 * This packet is used to send [encapsulated packets][EncapsulatedPacket]
 * that are in the send queue. This is where [ encapsulated packets][EncapsulatedPacket] get their name from, as they are encapsulated within
 * another container packet. The way these are used is by storing as many
 * packets in the send queue as possible into one packet before sending them off
 * all at once.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
open class CustomPacket : RakNetPacket {
    /**
     * The sequence ID of the packet.
     */
    var sequenceId = 0

    /**
     * If encoding, these are the packets that will be encoded into the packet.
     * <br></br>
     * If decoding, these are the packets decoded from the packet.
     */
    var messages: Array<EncapsulatedPacket>? = null

    /**
     * The encapsulated packets that require acknowledgement.
     */
    lateinit var ackMessages: Array<EncapsulatedPacket>

    /**
     * Creates a custom packet to be encoded.
     *
     * @param type
     * the type of custom packet being in between
     * `ID_CUSTOM_0` and `ID_CUSTOM_F`.
     * @throws IllegalArgumentException
     * if the `type` is not in between code
     * `ID_CUSTOM_0` and `ID_CUSTOM_F`.
     * @see .encode
     */
    protected constructor(type: Int) : super(type) {
        require(!(type < ID_CUSTOM_0 || type > ID_CUSTOM_F)) { "Custom packet ID must be in between ID_CUSTOM_0 and ID_CUSTOM_F" }
    }

    /**
     * Creates a `CUSTOM` packet to be decoded.
     *
     * @param packet
     * the original packet whose data will be read from in the
     * [.decode] method.
     */
    constructor(packet: Packet?) : super(packet!!) {}

    override fun encode() {
        writeTriadLE(sequenceId)
        if (messages != null) {
            val ackMessages = ArrayList<EncapsulatedPacket>()
            for (packet in messages!!) { //TODO
                if (packet.reliability.requiresAck()) {
                    packet.ackRecord = Record(sequenceId)
                    ackMessages.add(packet)
                }
                packet.encode(this)
            }
            this.ackMessages = ackMessages.toTypedArray()
        }
    }

    override fun decode() {
        sequenceId = readTriadLE()
        val messages = ArrayList<EncapsulatedPacket>()
        val ackMessages = ArrayList<EncapsulatedPacket>()
        while (remaining() >= EncapsulatedPacket.MINIMUM_SIZE) {
            val packet = EncapsulatedPacket()
            packet.decode(this)
            if (packet.reliability.requiresAck()) {
                packet.ackRecord = Record(sequenceId)
                ackMessages.add(packet)
            }
            messages.add(packet)
        }
        this.ackMessages = ackMessages.toTypedArray()
        this.messages = messages.toTypedArray()
    }

    companion object {
        /**
         * The minimum size of a custom packet.
         */
        const val MINIMUM_SIZE = 4

        /**
         * Calculates the size of the packet if it had been encoded.
         *
         * @param packets
         * the packets inside the custom packet.
         * @return the size of the packet if it had been encoded.
         */
        fun size(vararg packets: EncapsulatedPacket): Int {
            var size = 4
            if (packets != null) {
                for (packet in packets) {
                    size += packet.size()
                }
            }
            return size
        }

    }
}