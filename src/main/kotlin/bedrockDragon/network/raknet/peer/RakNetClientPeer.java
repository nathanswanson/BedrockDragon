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
package bedrockDragon.network.raknet.peer;

import static bedrockDragon.network.raknet.RakNetPacket.*;

import java.net.InetSocketAddress;

import bedrockDragon.network.raknet.RakNetPacket;
import bedrockDragon.network.raknet.protocol.ConnectionType;
import bedrockDragon.network.raknet.protocol.Reliability;
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest;
import bedrockDragon.network.raknet.protocol.login.ConnectionRequestAccepted;
import bedrockDragon.network.raknet.protocol.login.NewIncomingConnection;
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket;
import bedrockDragon.network.raknet.protocol.message.acknowledge.Record;
import bedrockDragon.network.raknet.server.RakNetServer;

import io.netty.channel.Channel;

/**
 * A client connection that handles login and other client related protocols.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
public final class RakNetClientPeer extends RakNetPeer {

	private final RakNetServer server;
	private long timestamp;

	/**
	 * Creates a RakNet client peer.
	 * 
	 * @param server
	 *            the server that is hosting the connection to the client.
	 * @param address
	 *            the address of the peer.
	 * @param guid
	 *            the globally unique ID of the peer.
	 * @param maximumTransferUnit
	 *            the maximum transfer unit of the peer.
	 * @param connectionType
	 *            the connection type of the peer.
	 * @param channel
	 *            the channel to communicate to the peer with.
	 */
	public RakNetClientPeer(RakNetServer server, ConnectionType connectionType, long guid, int maximumTransferUnit,
			Channel channel, InetSocketAddress address) {
		super(address, guid, maximumTransferUnit, connectionType, channel);
		this.server = server;
	}

	/**
	 * Returns the server this peer is connected to.
	 * 
	 * @return the server this peer is connected to.
	 */
	public RakNetServer getServer() {
		return this.server;
	}

	@Override
	public long getTimestamp() {
		if (this.isLoggedIn()) {
			return System.currentTimeMillis() - timestamp;
		}
		return -1L;
	}

	@Override
	public void handleMessage(RakNetPacket packet, int channel) {
		if (packet.getId() == ID_CONNECTION_REQUEST && this.isConnected()) {
			ConnectionRequest request = new ConnectionRequest(packet);
			request.decode();
			if (request.clientGuid == this.getGloballyUniqueId() && request.useSecurity == false) {
				ConnectionRequestAccepted requestAccepted = new ConnectionRequestAccepted();
				requestAccepted.clientAddress = this.getAddress();
				requestAccepted.clientTimestamp = request.timestamp;
				requestAccepted.serverTimestamp = server.getTimestamp();
				requestAccepted.encode();
				if (!requestAccepted.failed()) {
					this.sendMessage(Reliability.RELIABLE_ORDERED, requestAccepted);
					this.setState(RakNetState.HANDSHAKING);
				} else {
					server.disconnect(this,
							"Login failed (" + requestAccepted.getClass().getSimpleName() + " failed to encode)");
				}
			} else {
				String reason = "unknown error";
				if (request.clientGuid != this.getGloballyUniqueId()) {
					reason = "client GUID does not match";
				} else if (request.useSecurity == true) {
					reason = "client has security enabled";
				}
				this.sendMessage(Reliability.UNRELIABLE, ID_CONNECTION_ATTEMPT_FAILED);
				server.disconnect(this, "Login failed (" + reason + ")");
			}
		} else if (packet.getId() == ID_NEW_INCOMING_CONNECTION && this.getState() == RakNetState.HANDSHAKING) {
			NewIncomingConnection newIncomingConnection = new NewIncomingConnection(packet);
			newIncomingConnection.decode();
			if (!newIncomingConnection.failed()) {
				this.timestamp = System.currentTimeMillis() - newIncomingConnection.clientTimestamp;
				this.setState(RakNetState.LOGGED_IN);
				//this.getLogger().info("Client with globally unique ID "
				//		+ Long.toHexString(this.getGloballyUniqueId()).toUpperCase() + " has logged in");
				server.callEvent(listener -> listener.onLogin(server, this));
			} else {
				server.disconnect(this,
						"Failed to login (" + newIncomingConnection.getClass().getSimpleName() + " failed to decode)");
			}
		} else if (packet.getId() == ID_DISCONNECTION_NOTIFICATION) {
			server.disconnect(this, "Client disconnected");
		} else if (packet.getId() >= ID_USER_PACKET_ENUM) {
			server.callEvent(listener -> listener.handleMessage(server, this, packet, channel));
		} else {
			server.callEvent(listener -> listener.handleUnknownMessage(server, this, packet, channel));
		}
	}

	@Override
	public void onAcknowledge(Record record, EncapsulatedPacket packet) {
		server.callEvent(listener -> listener.onAcknowledge(server, this, record, packet));
	}

	@Override
	public void onNotAcknowledge(Record record, EncapsulatedPacket packet) {
		server.callEvent(listener -> listener.onLoss(server, this, record, packet));
	}

}
