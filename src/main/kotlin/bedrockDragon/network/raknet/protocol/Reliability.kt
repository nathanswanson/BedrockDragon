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
package bedrockDragon.network.raknet.protocol

import java.lang.IllegalArgumentException

/**
 * Represents a RakNet reliability. Reliabilities determine how packets are
 * handled when they are being sent or received.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
enum class Reliability(id: Int, reliable: Boolean, ordered: Boolean, sequenced: Boolean, requiresAck: Boolean) {
    /**
     * The packet will be sent, but it is not guaranteed that it will be
     * received.
     */
    UNRELIABLE(0, false, false, false, false),

    /**
     * Same as [.UNRELIABLE], however it will not be handled if a newer
     * sequenced packet on the channel has already arrived.
     */
    UNRELIABLE_SEQUENCED(1, false, false, true, false),

    /**
     * The packet will be sent and is guaranteed to be received at some point.
     */
    RELIABLE(2, true, false, false, false),

    /**
     * Same as [.RELIABLE], however it will not be handled until all
     * packets sent before it are also received.
     */
    RELIABLE_ORDERED(3, true, true, false, false),

    /**
     * Same as [RELIABLE], however it will not be handled if a newer
     * sequenced packet on the channel has already arrived.
     */
    RELIABLE_SEQUENCED(4, true, false, true, false),

    /**
     * Same as [.UNRELIABLE], however you will be notified whether the
     * packet was lost or received through the `onAcknowledge()` and
     * `onLoss()` methods found inside the
     * `RakNetServerListener` and `RakNetClientListener`
     * classes.
     *
     * @see com.whirvis.jraknet.server.RakNetServerListener RakNetServerListener
     *
     * @see com.whirvis.jraknet.client.RakNetClientListener RakNetClientListener
     */
    UNRELIABLE_WITH_ACK_RECEIPT(5, false, false, false, true),

    /**
     * Same as [.RELIABLE], however you will be notified when the packet
     * was received through the `onAcknowledge()` method through the
     * `RakNetServerListener` and `RakNetClientListener`
     * classes.
     *
     * @see com.whirvis.jraknet.server.RakNetServerListener RakNetServerListener
     *
     * @see com.whirvis.jraknet.client.RakNetClientListener RakNetClientListener
     */
    RELIABLE_WITH_ACK_RECEIPT(6, true, false, false, true),

    /**
     * Same as [.RELIABLE_ORDERED], however you will be notified when the
     * packet was received through the `onAcknowledge()` method
     * through the `RakNetServerListener` and
     * `RakNetClientListener` classes.
     *
     * @see com.whirvis.jraknet.server.RakNetServerListener RakNetServerListener
     *
     * @see com.whirvis.jraknet.client.RakNetClientListener RakNetClientListener
     */
    RELIABLE_ORDERED_WITH_ACK_RECEIPT(7, true, true, false, true),


    AUTOMATIC(8, true, true, false, false);
    /**
     * Returns the ID of the reliability.
     *
     * @return the ID of the reliability.
     */
    val id: Byte

    /**
     * Returns whether or not the reliability is reliable.
     *
     * @return `true` if the reliability is reliable,
     * `false` otherwise.
     */
    val isReliable: Boolean

    /**
     * Returns whether or not the reliability is ordered.
     *
     * @return `true` if the reliability is ordered,
     * `false` otherwise.
     */
    val isOrdered: Boolean

    /**
     * Returns whether not the reliability is sequenced.
     *
     * @return `true` if the reliability is sequenced,
     * `false` otherwise.
     */
    val isSequenced: Boolean
    private val requiresAck: Boolean

    /**
     * Returns whether or not the reliability requires acknowledgement.
     *
     * @return `true` if the reliability requires acknowledgement,
     * `false` otherwise.
     */
    fun requiresAck(): Boolean {
        return requiresAck
    }

    companion object {
        /**
         * Returns the reliability based on its ID.
         *
         * @param id
         * the ID of the reliability.
         * @return the reliability with the specified ID.
         */
		@JvmStatic
		fun lookup(id: Int): Reliability? {
            for (reliability in values()) {
                if (reliability.id.toInt() == id) {
                    return reliability
                }
            }
            return null
        }
    }

    /**
     * Constructs a `Reliability`.
     *
     * @param id
     * the ID of the reliability.
     * @param reliable
     * `true` if it is reliable, `false`
     * otherwise.
     * @param ordered
     * `true` if it is ordered, `false`
     * otherwise.
     * @param sequenced
     * `true` if it is sequenced, `false`
     * otherwise.
     * @param requiresAck
     * `true` if it requires an acknowledge receipt,
     * `false` otherwise.
     * @throws IllegalArgumentException
     * if both `ordered` and `sequenced` are
     * `true`.
     */
    init {
        this.id = id.toByte()
        isReliable = reliable
        isOrdered = ordered
        isSequenced = sequenced
        this.requiresAck = requiresAck
        require(!(ordered && sequenced)) { "A reliability cannot be both ordered and sequenced" }
    }
}