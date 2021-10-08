package bedrockDragon.network.raknet.protocol.packet

object PacketConstants {
    const val CONNECTED_PING = 0x00
    const val LOGIN_PACKET = 0x01
    const val CLIENT = 0x02
    const val CLIENT_TO_SERVER_HANDSHAKE_1 = 0x05
    const val SERVER_TO_CLIENT_HANDSHAKE_1 = 0x06
    const val CLIENT_TO_SERVER_HANDSHAKE_2 = 0x07
    const val SERVER_TO_CLIENT_HANDSHAKE_2 = 0x08
    const val CONNECTION_REQUEST = 0x09
    const val CONNECTION_REQUEST_ACCEPTED = 0x10
    const val NEW_INCOMING_CONNECTION = 0x13
    const val CLIENT_DISCONNECT = 0x15

}