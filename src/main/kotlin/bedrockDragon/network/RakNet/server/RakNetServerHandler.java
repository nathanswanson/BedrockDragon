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
package bedrockDragon.network.RakNet.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import bedrockDragon.network.RakNet.RakNetPacket;
import bedrockDragon.network.RakNet.peer.RakNetClientPeer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

/**
 * Used by the {@link RakNetServer} with the sole purpose of sending received
 * packets to the server so they can be handled. Any errors that occurs will
 * also be sent to the server to be dealt with.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
public final class RakNetServerHandler extends ChannelInboundHandlerAdapter {

	//private final Logger logger;
	private final RakNetServer server;
	private final ConcurrentHashMap<InetAddress, BlockedAddress> blocked;
	private InetSocketAddress causeAddress;

	/**
	 * Creates a RakNet server Netty handler.
	 * 
	 * @param server
	 *            the server to send received packets to.
	 */
	public RakNetServerHandler(RakNetServer server) {
		//this.logger = LogManager.getLogger(RakNetServer.class.getSimpleName() + "-"
		//		+ Long.toHexString(server.getGloballyUniqueId()).toUpperCase());
		this.server = server;
		this.blocked = new ConcurrentHashMap<InetAddress, BlockedAddress>();
	}

	/**
	 * Blocks the IP address. All currently connected clients with the IP
	 * address will be disconnected with the same reason that the IP address was
	 * blocked.
	 * 
	 * @param address
	 *            the IP address to block.
	 * @param reason
	 *            the reason the address was blocked. A <code>null</code> reason
	 *            will have <code>"Address blocked"</code> be used as the reason
	 *            instead.
	 * @param time
	 *            how long the address will blocked in milliseconds.
	 * @throws NullPointerException
	 *             if <code>address</code> is <code>null</code>.
	 */
	protected void blockAddress(InetAddress address, String reason, long time) throws NullPointerException {
		if (address == null) {
			throw new NullPointerException("Address cannot be null");
		}
		blocked.put(address, new BlockedAddress(time));
		for (RakNetClientPeer client : server.getClients()) {
			if (client.getInetAddress().equals(address)) {
				server.disconnect(client, reason == null ? "Address blocked" : reason);
			}
		}
		server.callEvent(listener -> listener.onBlock(server, address, reason, time));
		//logger.info("Blocked address " + address + " due to \"" + reason + "\" for " + time + " milliseconds");
	}

	/**
	 * Unblocks the IP address.
	 * 
	 * @param address
	 *            the IP address to unblock.
	 */
	protected void unblockAddress(InetAddress address) {
		if (address != null) {
			if (blocked.remove(address) != null) {
				server.callEvent(listener -> listener.onUnblock(server, address));
				//logger.info("Unblocked address " + address);
			}
		}
	}

	/**
	 * Returns whether or not the IP address is blocked.
	 * 
	 * @param address
	 *            the IP address.
	 * @return <code>true</code> if the IP address is blocked,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAddressBlocked(InetAddress address) {
		return blocked.containsKey(address);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DatagramPacket) {
			// Get packet and sender data
			DatagramPacket datagram = (DatagramPacket) msg;
			InetSocketAddress sender = datagram.sender();
			RakNetPacket packet = new RakNetPacket(datagram);

			// If an exception happens it's because of this address
			this.causeAddress = sender;

			// Check if address is blocked
			if (this.isAddressBlocked(sender.getAddress())) {
				BlockedAddress status = blocked.get(sender.getAddress());
				if (!status.shouldUnblock()) {
					datagram.release(); // No longer needed
					return; // Address still blocked
				}
				this.unblockAddress(sender.getAddress());
			}

			// Handle the packet and release the buffer
			server.handleMessage(sender, packet);
			//logger.debug("Sent packet to server and reset datagram buffer read position");
			server.callEvent(listener -> {
				datagram.content().readerIndex(0); // Reset index
				listener.handleNettyMessage(server, sender, datagram.content());
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
		server.handleHandlerException(this.causeAddress, cause);
	}

}
