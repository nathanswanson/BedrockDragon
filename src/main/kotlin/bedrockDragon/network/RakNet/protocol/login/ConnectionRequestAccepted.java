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
package bedrockDragon.network.RakNet.protocol.login;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import bedrockDragon.network.RakNet.Packet;
import bedrockDragon.network.RakNet.RakNet;
import bedrockDragon.network.RakNet.RakNetPacket;
import bedrockDragon.network.RakNet.protocol.Failable;

/**
 * A <code>CONNECTION_REQUEST_ACCEPTED</code> packet.
 * <p>
 * This packet is sent by the server during login after the
 * {@link ConnectionRequest CONNECTION_REQUEST} packet to indicate that a
 * client's connection has been accepted.
 * 
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
public final class ConnectionRequestAccepted extends RakNetPacket implements Failable {

	/**
	 * The address of the client that sent the connection request.
	 */
	public InetSocketAddress clientAddress;

	/**
	 * The RakNet system addresses.
	 */
	public InetSocketAddress[] systemAddresses;

	/**
	 * The client timestamp.
	 */
	public long clientTimestamp;

	/**
	 * The server timestamp.
	 */
	public long serverTimestamp;

	/**
	 * Whether or not the packet failed to encode/decode.
	 */
	private boolean failed;

	/**
	 * Creates a <code>CONNECTION_REQUEST_ACCEPTED</code> packet to be encoded.
	 * 
	 * @see #encode()
	 */
	public ConnectionRequestAccepted() {
		super(ID_CONNECTION_REQUEST_ACCEPTED);
		this.systemAddresses = new InetSocketAddress[RakNet.getSystemAddressCount()];
		for (int i = 0; i < systemAddresses.length; i++) {
			systemAddresses[i] = RakNet.SYSTEM_ADDRESS;
		}
	}

	/**
	 * Creates a <code>CONNECTION_REQUEST_ACCEPTED</code> packet to be decoded.
	 * 
	 * @param packet
	 *            the original packet whose data will be read from in the
	 *            {@link #decode()} method.
	 */
	public ConnectionRequestAccepted(Packet packet) {
		super(packet);
	}

	@Override
	public void encode() {
		try {
			this.writeAddress(clientAddress);
			this.writeShort(0);
			for (int i = 0; i < systemAddresses.length; i++) {
				this.writeAddress(systemAddresses[i]);
			}
			this.writeLong(clientTimestamp);
			this.writeLong(serverTimestamp);
		} catch (UnknownHostException e) {
			this.clientAddress = null;
			this.clientTimestamp = 0;
			this.serverTimestamp = 0;
			this.clear();
			this.failed = true;
		}
	}

	@Override
	public void decode() {
		try {
			this.clientAddress = this.readAddress();
			this.readShort(); // TODO: Discover usage
			this.systemAddresses = new InetSocketAddress[RakNet.getSystemAddressCount()];
			for (int i = 0; i < systemAddresses.length; i++) {
				if (this.remaining() >= 16) {
					this.systemAddresses[i] = this.readAddress();
				} else {
					this.systemAddresses[i] = RakNet.SYSTEM_ADDRESS;
				}
			}
			this.clientTimestamp = this.readLong();
			this.serverTimestamp = this.readLong();
		} catch (UnknownHostException e) {
			this.clientAddress = null;
			this.clientTimestamp = 0;
			this.serverTimestamp = 0;
			this.clear();
			this.failed = true;
		}
	}

	@Override
	public boolean failed() {
		return this.failed;
	}

}
