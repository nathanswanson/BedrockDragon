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

import bedrockDragon.network.raknet.identifier.Identifier
import bedrockDragon.network.raknet.protocol.ConnectionType
import java.net.InetSocketAddress
import java.util.Objects

/**
 * Contains information about a server ping such as who sent the ping and what
 * the server will respond back with.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class ServerPing
/**
 * Creates a server ping.
 *
 * @param sender
 * the address of the ping sender.
 * @param connectionType
 * the connection type of the ping sender.
 * @param identifier
 * the identifier to respond with.
 */(
    /**
     * Returns the address of the ping sender.
     *
     * @return the address of the ping sender.
     */
    val sender: InetSocketAddress,
    /**
     * Returns the connection type of the ping sender.
     *
     * @return the connection type of the ping sender.
     */
    val connectionType: ConnectionType,
    /**
     * Sets the identifier being sent back to the sender.
     *
     * @param identifier
     * the new identifier.
     */
    var identifier: Identifier
) {

    /**
     * Returns the identifier being sent back to the sender.
     *
     * @return the identifier being sent back to the sender.
     */
    override fun hashCode(): Int {
        return Objects.hash(sender, connectionType, identifier)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is ServerPing) {
            return false
        }
        val sp = o
        return (sender == sp.sender && connectionType == sp.connectionType
                && identifier == sp.identifier)
    }

    override fun toString(): String {
        return ("ServerPing [sender=" + sender + ", identifier=" + identifier + ", connectionType=" + connectionType
                + "]")
    }
}