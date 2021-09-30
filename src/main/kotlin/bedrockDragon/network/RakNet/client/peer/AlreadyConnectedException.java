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
package bedrockDragon.network.RakNet.client.peer;

import java.net.InetSocketAddress;

import bedrockDragon.network.RakNet.client.RakNetClient;

/**
 * Signals that a {@link RakNetClient} has attempted to connect to a server that
 * it is already connected to.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.0.0
 */
public final class AlreadyConnectedException extends PeerFactoryException {

	private static final long serialVersionUID = -482118372058339060L;

	private final InetSocketAddress address;

	/**
	 * Constructs an <code>AlreadyConnectedException</code>.
	 * 
	 * @param client
	 *            the client that is already connected to the server.
	 * @param address
	 *            the address of the server that the client is already connected
	 *            to.
	 */
	public AlreadyConnectedException(RakNetClient client, InetSocketAddress address) {
		super(client, "Already connected to server");
		this.address = address;
	}

	/**
	 * Returns the address of the server that the client is already connected
	 * to.
	 * 
	 * @return the address of the server that the client is already connected
	 *         to.
	 */
	public InetSocketAddress getAddress() {
		return this.address;
	}

}
