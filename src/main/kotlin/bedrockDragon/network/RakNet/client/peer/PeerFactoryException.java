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

import bedrockDragon.network.RakNet.client.RakNetClient;
import bedrockDragon.network.RakNet.client.RakNetClientException;

/**
 * Signals that an error has occurred while a {@link PeerFactory} was attempting
 * to create a {@link com.whirvis.jraknet.peer.RakNetServerPeer
 * RakNetServerPeer}.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.11.0
 */
public class PeerFactoryException extends RakNetClientException {

	private static final long serialVersionUID = -5025319984358819345L;

	/**
	 * Constructs a <code>PeerFactoryException</code>.
	 * 
	 * @param client
	 *            the client that created the peer that threw the exception.
	 * @param error
	 *            the detail message.
	 */
	public PeerFactoryException(RakNetClient client, String error) {
		super(client, error);
	}

	/**
	 * Constructs a <code>PeerFactoryException</code>.
	 * 
	 * @param client
	 *            the client that created the peer that threw the exception.
	 * @param error
	 *            the <code>Throwable</code> that was thrown.
	 */
	public PeerFactoryException(RakNetClient client, Throwable error) {
		super(client, error);
	}

}
