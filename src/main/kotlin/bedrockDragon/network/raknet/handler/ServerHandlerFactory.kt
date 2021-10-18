package bedrockDragon.network.raknet.handler

import bedrockDragon.network.raknet.handler.handlertype.connect.ConnectionRequestHandlerPost
import bedrockDragon.network.raknet.handler.handlertype.connect.IncomingConnectionHandler
import bedrockDragon.network.raknet.handler.handlertype.login.ConnectionRequestHandlerOne
import bedrockDragon.network.raknet.handler.handlertype.login.ConnectionRequestHandlerTwo
import bedrockDragon.network.raknet.handler.handlertype.login.LoginHandler
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.peer.RakNetPeer
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.raknet.handler.handlertype.connect.ConnectedPingHandler
import bedrockDragon.network.raknet.peer.RakNetClientPeer
import io.netty.channel.Channel
import mu.KotlinLogging
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

class ServerHandlerFactory {
    companion object {

        private val logger = KotlinLogging.logger {}

        fun createPacketHandle(sender: InetSocketAddress, packet: RakNetPacket, channel: Channel) : PacketHandler {
            return when(packet.id.toInt()) {
                PacketConstants.UNCONNECTED_PING -> LoginHandler(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_1 -> ConnectionRequestHandlerOne(sender, packet, channel)
                PacketConstants.CLIENT_TO_SERVER_HANDSHAKE_2 -> ConnectionRequestHandlerTwo(sender, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }

        }

        fun createEncapsulatedPacketHandle(sender: RakNetPeer, packet: EncapsulatedPacket, channel: Channel) : PacketHandler {
            return when(packet.payload.buffer().getUnsignedByte(0).toInt()) {
                PacketConstants.CONNECTED_PING -> ConnectedPingHandler(sender, packet, channel)
                PacketConstants.CONNECTION_REQUEST -> ConnectionRequestHandlerPost(sender, packet, channel)
                PacketConstants.NEW_INCOMING_CONNECTION -> IncomingConnectionHandler(sender as RakNetClientPeer, packet, channel)
                else -> throw IllegalArgumentException("Unknown packet sent to factory.")
            }

        }

      /*  fun createGamePacketHandle(sender: RakNetPeer, packet: Packet, channel: Channel) {
            logger.info {
                packet.buffer().getUnsignedByte(0)
            }

            //We have no idea how big the decompressed packet will be so, I use an array list
            val inflater = Inflater()
            inflater.setInput(packet.buffer().nioBuffer())

            var totalDecompressedBytes = 0
            val decompressedBytes = ArrayList<Byte>()

            try {
                while(!inflater.finished()) {
                    val byteDecompressedBuffer = ByteArray(packet.size())
                    val bytesDecompressedCount = inflater.inflate(byteDecompressedBuffer)

                    totalDecompressedBytes += bytesDecompressedCount
                    for(byte in byteDecompressedBuffer) {
                        decompressedBytes.add(byte)
                    }
                }
            } catch (e: DataFormatException) {
                e.printStackTrace()
            }


        }*/

        //CLIENT PACKETS FOR CLIENT SIMULATION


    }
}