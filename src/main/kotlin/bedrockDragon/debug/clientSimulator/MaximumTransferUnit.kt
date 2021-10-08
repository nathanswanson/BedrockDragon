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
package bedrockDragon.debug.clientSimulator

import kotlin.Throws
import java.lang.IllegalStateException
import java.util.Objects
import java.lang.NullPointerException
import bedrockDragon.network.raknet.map.IntMap
import java.util.TreeMap
import java.lang.IllegalArgumentException
import java.util.ArrayList

/**
 * Used by the [RakNetClient] and
 * [PeerFactory][bedrockDragon.debug.clientSimulator.peer.PeerFactory] during
 * connection to track how and when they should modify their maximum transfer
 * unit.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.0.0
 */
class MaximumTransferUnit(size: Int, retries: Int) {
    /**
     * Returns the size of the maximum transfer unit in bytes.
     *
     * @return the size of the maximum transfer unit in bytes.
     */
    val size: Int

    /**
     * Returns the default amount of retries before the client stops using this
     * maximum transfer unit size and uses the next lowest one.
     *
     * @return the default amount of retries before the client stops using this
     * maximum transfer unit size and uses the next lowest one.
     */
    val retries: Int

    /**
     * Returns the amount of times [.retry] can be called before
     * [.reset] needs to be called. If this is `0`, then
     * calling the [.retry] method will yield a
     * `IllegalStateException`.
     *
     * @return the amount of times [.retry] can be called before
     * [.reset] needs to be called.
     */
    var retriesLeft: Int
        private set

    /**
     * Lowers the amount of retries left.
     *
     * @return the amount of retries left.
     * @throws IllegalStateException
     * if the amount of retries left is than or equal to
     * `0`.
     * @see .reset
     */
    @Throws(IllegalStateException::class)
    fun retry(): Int {
        check(retriesLeft >= 0) { "No more retries left, use reset() in order to reuse a maximum transfer unit" }
        return retriesLeft--
    }

    /**
     * Sets the amount of retries left back to the default.
     *
     *
     * This is necessary in order to be able to reuse a maximum transfer unit
     * once it has been depleted of its retries left.
     *
     * @see .retry
     */
    fun reset() {
        if (retriesLeft != retries) {
            retriesLeft = retries
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(size, retries)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is MaximumTransferUnit) {
            return false
        }
        val mtu = o
        return size == mtu.size && retries == mtu.retries
    }

    override fun toString(): String {
        return "MaximumTransferUnit [size=$size, retries=$retries, retriesLeft=$retriesLeft]"
    }

    companion object {
        /**
         * Sorts an array of maximum transfer units from the highest to lowest
         * maximum transfer units based on their maximum transfer unit size.
         *
         * @param units
         * the maximum transfer units to sort.
         * @return the sorted maximum transfer units.
         * @throws NullPointerException
         * if the `units` are `null` or if any of
         * the maximum transfer units inside of the `unit`
         * are `null`.
         */
        @Throws(NullPointerException::class)
        fun sort(vararg units: MaximumTransferUnit?): Array<MaximumTransferUnit?> {
            if (units == null) {
                throw NullPointerException("Units cannot be null")
            } else if (units.size <= 0) {
                return arrayOfNulls(0) // Nothing to sort
            }
            val unitMap = IntMap<MaximumTransferUnit>()
            for (unit in units) {
                if (unit == null) {
                    throw NullPointerException("Maximum transfer unit cannot be null")
                }
                unitMap[unit.size] = unit
            }
            val unitList = ArrayList<MaximumTransferUnit?>()
            val unitTreeMap = TreeMap(unitMap)
                .descendingMap()
            val unitSet: MutableSet<MutableMap.MutableEntry<Int?, MaximumTransferUnit>> = unitTreeMap.entries
            val unitI = unitSet.iterator()
            while (unitI.hasNext()) {
                val (_, value) = unitI.next()
                unitList.add(value)
            }
            return unitList.toTypedArray()
        }
    }

    /**
     * Creates a maximum transfer unit.
     *
     * @param size
     * the size of the maximum transfer unit in bytes.
     * @param retries
     * the amount of time the client should try to use it before
     * going to the next lowest maximum transfer unit size.
     * @throws IllegalArgumentException
     * if the `size` is less than or equal to
     * `0`, if the `size` odd (not divisible
     * by `2`), or the retry count is less than or equal
     * to `0`.
     */
    init {
        require(size > 0) { "Size must be greater than 0" }
        require(size % 2 == 0) { "Size cannot be odd" }
        this.size = size
        this.retries = retries
        retriesLeft = retries
    }
}