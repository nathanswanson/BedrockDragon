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

import bedrockDragon.network.raknet.RakNetException

/**
 * Signals that an error has occurred in a [RakNetClient]
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.0.0
 */
open class RakNetClientException : RakNetException {
    /**
     * Returns the client that threw the exception.
     *
     * @return the client that threw the exception.
     */
    val client: RakNetClient?

    /**
     * Constructs a `RakNetClientException`.
     *
     * @param client
     * the client that threw the exception.
     * @param error
     * the detail message.
     */
    constructor(client: RakNetClient?, error: String?) : super(error) {
        this.client = client
    }

    /**
     * Constructs a `RakNetClientException`.
     *
     * @param client
     * the client that threw the exception.
     * @param error
     * the `Throwable` that was thrown.
     */
    constructor(client: RakNetClient?, error: Throwable?) : super(error) {
        this.client = client
    }

    companion object {
        private const val serialVersionUID = 2441122006497992080L
    }
}