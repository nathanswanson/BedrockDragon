package bedrockDragon.debug.junit

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.message.CustomFourPacket
import bedrockDragon.network.raknet.protocol.message.CustomPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.collections.ArrayList

class EncapsulatedPacketTest {

    @TestFactory
    fun writeEncapsulated(): List<DynamicTest> {
        val test = ArrayList<DynamicTest>()
        val message = generatePsuedoPacket()


        for (reliability in Reliability.values()) {
            test.add(
                DynamicTest.dynamicTest(reliability.toString()) {
                    val enPacket = EncapsulatedPacket()
                    enPacket.payload = message
                    enPacket.reliability = reliability
                    enPacket.orderChannel = 0
                    enPacket.messageIndex = 0
                    val cPacket = CustomFourPacket()
                    cPacket.messages = arrayOf(enPacket)
                    cPacket.sequenceId = 0
                    cPacket.encode()
                    cPacket.decode()


                    assert(cPacket.messages == message)

                }
            )
        }
        //permutate for each reliability
        return test
    }

    @Test
    fun customPacket() {
        val packet = generatePsuedoPacket()
        val customPacket = CustomFourPacket()



    }

    fun generatePsuedoPacket(): RakNetPacket {
        val message = ConnectionRequest()
        val clientGuid = UUID.randomUUID().leastSignificantBits
        val timestamp = System.currentTimeMillis()
        val useSecurity = false

        message.clientGuid = clientGuid
        message.timestamp = timestamp
        message.useSecurity = useSecurity
        message.encode()

        return message
    }

}