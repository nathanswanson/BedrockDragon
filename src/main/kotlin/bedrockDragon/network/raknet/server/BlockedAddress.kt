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
package bedrockDragon.network.raknet.server

/**
 * An address that the server has blocked.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class BlockedAddress(time: Long) {
    /**
     * Returns the time the address was first blocked. This value is the time
     * that the original blocked address object was created, according to
     * [System.currentTimeMillis].
     *
     * @return the time the address was first blocked.
     */
    val startTime: Long

    /**
     * Returns the amount of time the address has been blocked.
     *
     * @return the amount of time the address has been blocked.
     */
    val time: Long

    /**
     * Returns whether or not the blocked address should be unblocked.
     *
     * @return `true` if the address should be unblocked,
     * `false` otherwise.
     */
    fun shouldUnblock(): Boolean {
        return if (time <= PERMANENT_BLOCK) {
            false // The address has been permanently blocked
        } else System.currentTimeMillis() - startTime >= time
    }

    override fun toString(): String {
        return "BlockedAddress [startTime=$startTime, time=$time]"
    }

    companion object {
        /**
         * The address is blocked permanently.
         */
        const val PERMANENT_BLOCK = -1L
    }

    /**
     * Creates a blocked address.
     *
     * @param time
     * the amount of time until the client is unblocked in
     * milliseconds.
     * @throws IllegalArgumentException
     * if the `time` is less than `0` and is
     * not equal to {@value #PERMANENT_BLOCK}.
     */
    init {
        require(!(time <= 0 && time != PERMANENT_BLOCK)) { "Block time must be greater than 0 or equal to $PERMANENT_BLOCK for a permanent block" }
        startTime = System.currentTimeMillis()
        this.time = time
    }
}