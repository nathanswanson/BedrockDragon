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
package bedrockDragon.network.raknet.protocol.status;

import bedrockDragon.network.raknet.Packet;
import bedrockDragon.network.raknet.RakNetPacket;

/**
 * A <code>CONNECTED_PONG</code> packet.
 * <p>
 * This packet is sent in response of a {@link ConnectedPing CONNECTED_PING}
 * packet ensuring the sender that the connection is still active.
 * 
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
public final class ConnectedPong extends RakNetPacket {

	/**
	 * The timestamp of the sender of the ping.
	 */
	public long timestamp;

	/**
	 * The timestamp of the sender of the pong.
	 */
	public long timestampPong;

	/**
	 * Creates a <code>CONNECTED_PONG</code> packet to be encoded.
	 * 
	 * @see #encode()
	 */
	public ConnectedPong() {
		super(ID_CONNECTED_PONG);
	}

	/**
	 * Creates a <code>CONNECTED_PONG</code> packet to be decoded.
	 * 
	 * @param packet
	 *            the original packet whose data will be read from in the
	 *            {@link #decode()} method.
	 */
	public ConnectedPong(Packet packet) {
		super(packet);
	}

	@Override
	public void encode() {
		this.writeLong(timestamp);
		this.writeLong(timestampPong);
	}

	@Override
	public void decode() {
		this.timestamp = this.readLong();
		this.timestampPong = -1L;
		if (this.remaining() >= Long.BYTES) {
			this.timestampPong = this.readLong();
		}
	}

}
