package bedrockDragon.debug.junit

import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.collections.ArrayList

class EncapsulatedPacketTest {

    @TestFactory
    fun writeEncapsulated(): List<DynamicTest> {
        val test = ArrayList<DynamicTest>()

        val message = ConnectionRequest()
        val clientGuid = UUID.randomUUID().leastSignificantBits
        val timestamp = System.currentTimeMillis()
        val useSecurity = false

        message.clientGuid = clientGuid
        message.timestamp = timestamp
        message.useSecurity = useSecurity
        message.encode()

        for (reliability in Reliability.values()) {
            test.add(
                DynamicTest.dynamicTest(reliability.toString()) {
                    val enPacket = EncapsulatedPacket()
                    enPacket.payload = message
                    enPacket.reliability = reliability
                    enPacket.orderChannel = 0

                    enPacket.encode()
                    enPacket.decode()

                    assert(enPacket.payload.equals(message))
                    assert(enPacket.reliability.equals(reliability))
                    assert(enPacket.orderChannel.toInt() == 0)
                }
            )
        }
        //permutate for each reliability
    }
}