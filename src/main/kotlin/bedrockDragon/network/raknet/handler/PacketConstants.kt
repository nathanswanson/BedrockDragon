package bedrockDragon.network.raknet.handler

object PacketConstants {
    const val CONNECTED_PING = 0x00
    const val UNCONNECTED_PING = 0x01
    const val UNCONNECTED_REQUIRE_OPEN_PING = 0x02
    const val CONNECTED_PONG = 0x03
    const val CLIENT_TO_SERVER_HANDSHAKE_1 = 0x05
    const val SERVER_TO_CLIENT_HANDSHAKE_1 = 0x06
    const val CLIENT_TO_SERVER_HANDSHAKE_2 = 0x07
    const val SERVER_TO_CLIENT_HANDSHAKE_2 = 0x08
    const val CONNECTION_REQUEST = 0x09
    const val CONNECTION_REQUEST_ACCEPTED = 0x10
    const val NEW_INCOMING_CONNECTION = 0x13
    const val CLIENT_DISCONNECT = 0x15
    const val INCOMPATIBLE_PROTOCOL = 0x19
    const val UNCONNECTED_PONG = 0x1c
    val CUSTOM_PACKET_RANGE = 0x80..0x8d
    const val NACK = 0xa0
    const val ACK = 0xc0
    const val GAME_PACKET = 0xfe

    //Unverified

    const val ALREADY_CONNECTED = 0x12
    const val NO_FREE_INCOMING_CONNECTIONS = 0x14
    const val CONNECTION_BANNED = 0x17
}