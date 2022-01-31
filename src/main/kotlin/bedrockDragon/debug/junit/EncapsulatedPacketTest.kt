/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
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

package bedrockDragon.debug.junit

import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.login.ConnectionRequest
import bedrockDragon.network.raknet.protocol.message.CustomFourPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.util.*
import kotlin.collections.ArrayList

class EncapsulatedPacketTest {

    @TestFactory
    fun writeEncapsulated(): List<DynamicTest> {
        val test = ArrayList<DynamicTest>()
        val message = generatePseudoPacket()


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
        val packet = generatePseudoPacket()
        val customPacket = CustomFourPacket()



    }

    private fun generatePseudoPacket(): RakNetPacket {
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