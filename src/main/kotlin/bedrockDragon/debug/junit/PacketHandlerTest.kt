package bedrockDragon.debug.junit

import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.packet.PacketConstants
import bedrockDragon.network.raknet.protocol.status.UnconnectedPing
import org.junit.jupiter.api.Test
import java.util.*

internal class PacketHandlerTest {

    @Test
    fun connectionRequest() {
        val connect = ConnectionRequest()
        val clientGuid = UUID.randomUUID().leastSignificantBits
        val timestamp = System.currentTimeMillis()
        val useSecurity = false

        connect.clientGuid = clientGuid
        connect.timestamp = timestamp
        connect.useSecurity = useSecurity

        connect.encode()

        //Strip and check ID before decode
        assert(connect.buffer().readByte() == PacketConstants.CONNECTION_REQUEST.toByte())
        connect.decode()

        assert(clientGuid == connect.clientGuid)
        assert(timestamp == connect.timestamp)
        assert(useSecurity == connect.useSecurity)

    }

    @Test
    fun unconnectedPing() {
        val connect = UnconnectedPing()
        val clientGuid = UUID.randomUUID().leastSignificantBits
        val timestamp = System.currentTimeMillis()
        val useSecurity = false

        connect.timestamp = timestamp
        connect.pingId = clientGuid
        connect.encode()

        //Strip and check ID before decode
        assert(connect.buffer().readByte() == PacketConstants.LOGIN_PACKET.toByte())
        connect.decode()

        assert(timestamp == connect.timestamp)
        assert(clientGuid == connect.pingId)
    }
}