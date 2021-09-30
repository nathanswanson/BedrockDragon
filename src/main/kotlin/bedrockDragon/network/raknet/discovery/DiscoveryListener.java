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
package bedrockDragon.network.raknet.discovery;

import java.net.InetSocketAddress;

import bedrockDragon.network.raknet.identifier.Identifier;

import io.netty.buffer.ByteBuf;

/**
 * Used to listen for events that occur in the {@link Discovery} system. In
 * order to listen for events, one must use the
 * {@link Discovery#addListener(DiscoveryListener)} method.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.11.0
 * @see DiscoveredServer
 * @see Identifier
 */
public interface DiscoveryListener {

	/**
	 * Called when a server is discovered on the local network.
	 * 
	 * @param server
	 *            the newly discovered server.
	 */
	public default void onServerDiscovered(DiscoveredServer server) {
	}

	/**
	 * Called when the identifier of an already discovered server changes.
	 * 
	 * @param server
	 *            the server whose identifier has updated.
	 * @param oldIdentifier
	 *            the old identifier.
	 */
	public default void onServerIdentifierUpdate(DiscoveredServer server, Identifier oldIdentifier) {
	}

	/**
	 * Called when a previously discovered server has been forgotten.
	 * 
	 * @param server
	 *            the server that was forgotten.
	 */
	public default void onServerForgotten(DiscoveredServer server) {
	}

	/**
	 * Called when the handler receives a packet after the server has already
	 * handled it.
	 * <p>
	 * This method is useful for handling packets outside of the RakNet
	 * protocol. All packets received here have already been handled by the
	 * server.
	 * 
	 * @param address
	 *            the address of the sender.
	 * @param buf
	 *            the buffer of the received packet.
	 */
	public default void handleNettyMessage(InetSocketAddress address, ByteBuf buf) {
	}

}
