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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import bedrockDragon.network.raknet.RakNetPacket;
import bedrockDragon.network.raknet.protocol.status.UnconnectedPong;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

/**
 * Used by the {@link Discovery} system with the sole purpose of sending
 * received packets to the discovery system so they can be handled. If any
 * errors occur while handling a packet, it will be ignored.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.11.0
 */
public final class DiscoveryHandler extends ChannelInboundHandlerAdapter {

	//private Logger logger;
	private final ArrayList<InetAddress> blocked;
	private InetSocketAddress causeAddress;

	/**
	 * Creates a discovery system Netty handler.
	 */
	protected DiscoveryHandler() {
	//	this.logger = LogManager.getLogger(DiscoveryHandler.class);
		this.blocked = new ArrayList<InetAddress>();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof DatagramPacket) {
			// Get packet and sender data
			DatagramPacket datagram = (DatagramPacket) msg;
			InetSocketAddress sender = datagram.sender();
			RakNetPacket packet = new RakNetPacket(datagram);

			// If an exception happens it's because of this address
			this.causeAddress = sender;

			// Check if the address is blocked
			if (blocked.contains(sender.getAddress())) {
				datagram.release(); // No longer needed
				return; // Address blocked
			}

			// Handle the packet and release the buffer
			if (packet.getId() == RakNetPacket.ID_UNCONNECTED_PONG) {
				UnconnectedPong pong = new UnconnectedPong(packet);
				pong.decode();
				if (!pong.failed()) {
					Discovery.updateDiscoveryData(sender, pong);
				//	logger.trace("Sent unconnected pong to discovery system");
				}
			}
			Discovery.callEvent(listener -> {
				datagram.content().readerIndex(0); // Reset index
				listener.handleNettyMessage(sender, datagram.content());
			});
			if (datagram.release() /* No longer needed */) {
			//	logger.trace("Released datagram");
			} else {
			//	logger.error("Memory leak: Failed to deallocate datagram when releasing it");
			}

			// No exceptions occurred, release the suspect
			this.causeAddress = null;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (!blocked.contains(causeAddress.getAddress())) {
			blocked.add(causeAddress.getAddress());
		//	logger.warn(
			//		"Blocked address " + causeAddress.getAddress() + " that caused " + cause.getClass().getSimpleName()
			//				+ " to be thrown, discovering servers from it will no longer be possible");
		} else {
			//logger.error("Blocked address still cause exception to be thrown", cause);
		}
	}

}
