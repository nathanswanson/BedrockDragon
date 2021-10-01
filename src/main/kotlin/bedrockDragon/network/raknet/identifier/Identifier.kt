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
package bedrockDragon.network.raknet.identifier

import bedrockDragon.network.raknet.protocol.ConnectionType
import java.util.Objects

/**
 * Represents an identifier sent from a server in response to a client ping. Any
 * classes that extends this class must override the [.build] method in
 * order to make use of the identifier capabilities.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
open class Identifier : Cloneable {
    private val identifier: String

    /**
     * Returns the connection type of the sender/creator of the identifier.
     *
     * @return the connection type of the sender/creator of the identifier.
     */
    val connectionType: ConnectionType

    /**
     * Creates an identifier.
     *
     * @param identifier
     * the identifier text.
     * @param connectionType
     * the protocol implementation that sent the identifier.
     */
    constructor(identifier: String, connectionType: ConnectionType) {
        this.identifier = identifier
        this.connectionType = connectionType
    }

    /**
     * Creates an identifier with the connection type defaulting to the
     * [ ConnectionType.JRAKNET][bedrockDragon.network.raknet.protocol.ConnectionType.JRAKNET] connection type.
     *
     * @param identifier
     * the identifier text.
     */
    constructor(identifier: String) {
        this.identifier = identifier
        connectionType = ConnectionType.JRAKNET
    }

    /**
     * Creates an identifier from another identifier.
     *
     * @param identifier
     * the identifier to grab the information from.
     */
    constructor(identifier: Identifier) {
        this.identifier = identifier.identifier
        connectionType = identifier.connectionType
    }

    /**
     * Creates an identifier with the identifier text being set to
     * `null` and the connection type defaulting to the
     * [ ConnectionType.JRAKNET][bedrockDragon.network.raknet.protocol.ConnectionType.JRAKNET] connection type.
     */
    constructor() {
        identifier = null.toString()
        connectionType = ConnectionType.JRAKNET
    }

    /**
     * Returns the identifier text.
     *
     * @return the identifier text.
     */
    open fun build(): String {
        return identifier
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, connectionType)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is Identifier) {
            return false
        }
        val i = o
        return identifier == i.identifier && connectionType == i.connectionType
    }

    override fun toString(): String {
        return this.build()
    }

    public override fun clone(): Any {
        return Identifier(this.build(), connectionType)
    }
}