package bedrockDragon.network.bedrockprotocol

object PacketConstants {
    const val LOGIN_PACKET = 0x01
    const val CLIENT = 0x02
    const val CLIENT_TO_SERVER_HANDSHAKE_1 = 0x05
    const val SERVER_TO_CLIENT_HANDSHAKE_1 = 0x06
    const val CLIENT_TO_SERVER_HANDSHAKE_2 = 0x07
    const val SERVER_TO_CLIENT_HANDSHAKE_2 = 0x08
    const val CONNECTION_REQUEST = 0x09
    const val CONNECTION_REQUEST_ACCEPTED = 0x0A

}