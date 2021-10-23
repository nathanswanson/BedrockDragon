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
package bedrockDragon.network.raknet.peer

import bedrockDragon.DragonServer
import bedrockDragon.network.PlayerStatus
import bedrockDragon.network.auth.MojangAuth
import bedrockDragon.network.raknet.Packet
import bedrockDragon.network.raknet.RakNetPacket
import bedrockDragon.network.raknet.game.GamePacket
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.handler.minecraft.PlayStatusHandler
import bedrockDragon.network.raknet.handler.minecraft.ResourcePackInfoHandler
import bedrockDragon.network.raknet.protocol.RaknetConnectionStatus
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.MinecraftLoginPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PlayStatusPacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.reactive.ReactSocket
import bedrockDragon.network.zlib.PacketCompression
import bedrockDragon.reactive.player.PlayerObservable
import com.nimbusds.jose.JWSObject
import io.netty.channel.Channel
import io.reactivex.rxjava3.core.Observable
import org.jetbrains.annotations.Nullable
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

class RakNetClientPeer(val server: DragonServer, connectionType: ConnectionType, guid: Long, maximumTransferUnit: Int, channel: Channel, val sender: InetSocketAddress)
    : RakNetPeer(sender, guid, maximumTransferUnit, connectionType, channel) {

    //TODO status safety
    var status: RaknetConnectionStatus = RaknetConnectionStatus.DISCONNECTED
    var observer: Observable<Any> = Observable.empty()
    private var clientPeer : MinecraftClientPeer? = null


    public fun setMinecraftClient(protocol: Int, chainData: List<JWSObject>, skinData: String) {
        clientPeer = MinecraftClientPeer(protocol,chainData,skinData, observer)
    }
    /**
     * When a registered client sends a packet this function is called with that packet
     */

    override fun handleEncapsulatedPacket(packet: EncapsulatedPacket): EncapsulatedPacket {

        if(packet.payload.buffer().getUnsignedByte(0).toInt() == PacketConstants.CLIENT_DISCONNECT) {
            server.disconnect(this, "")
        }

        val packetUnSplit = super.handleEncapsulatedPacket(packet)
        //Client has connected at this point
        if(packetUnSplit.payload.buffer().getUnsignedByte(0).toInt() == PacketConstants.GAME_PACKET) {
            if(!packetUnSplit.split)
            {
                packetUnSplit.payload.buffer().readUnsignedByte()
                MinecraftPacketFactory().createIncomingPacketHandler(clientPeer , packetUnSplit)
            }
        } else {
            val handler = DragonServer.ServerHandlerFactory.createEncapsulatedPacketHandle(this, packetUnSplit, channel)
            handler.responseToClient()
            handler.responseToServer()
        }
        //TODO make sure it returns clone not original
        return packetUnSplit
    }

    private fun bedrockClient(): MinecraftClientPeer? {
        return clientPeer
    }

    private inner class MinecraftClientPeer(val protocol: Int, val playerData: List<JWSObject>, val skinData: String, override var observable: Observable<Any>): ReactSocket<PlayerObservable>, MinecraftPeer() {
        var xuid: Long = 0
        var uuid: String = ""
        var userName: String = ""
        var status = PlayerStatus.Connected

        init {
            for(jwt in playerData) {
                val jsonJwt = jwt.payload.toJSONObject()
                if(jsonJwt.containsKey("extraData")) {
                    //very unsafe checks here
                    //TODO
                    val extra = jsonJwt["extraData"] as Map<*,*>
                    xuid = (extra["XUID"] as String).toLong()
                    uuid = extra["identity"] as String
                    userName = extra["displayName"] as String
                }
            }

            //Player status is moving from reactive to ping-pong netty
            //TODO

            if(MojangAuth.verifyXUIDFromChain(playerData)) {
                status = PlayerStatus.Authenticated
            }

            if(userName.length !in 3..16)
            {
                status = PlayerStatus.PendDisconnect
            }

            if(!userName.matches(Regex("^[a-zA-Z0-9_ ]*$"))) {
                status = PlayerStatus.PendDisconnect
            }

            if(status == PlayerStatus.Authenticated) {
                status = PlayerStatus.LoadingGame
            }
        }
    }

    private inner class MinecraftPacketFactory {
        fun createIncomingPacketHandler(@Nullable client: MinecraftPeer?, packet: EncapsulatedPacket) {
            val buf = packet.payload.buffer()
            try {
                val bytes = ByteArray(buf.readableBytes())
                buf.readBytes(bytes)
                //removes zlib compression
                val decompressed = PacketCompression.decompress(
                    bytes
                )

                val inGamePacket = GamePacket()
                inGamePacket.decode(Packet(decompressed))

                if(clientPeer != null && clientPeer!!.status == PlayerStatus.InGame) {
                    //if packet is non-reflective send the packet to the observer deck.
                    //packet is converted into dragon protocol which is an Observable
                    //protocol request will then wait its turn to be broadcast by the reactor and
                    // then filtered through the great mesh
                }
                when(inGamePacket.gamePacketId) {
                    MinecraftPacketConstants.LOGIN -> {
                        val loginPacket = MinecraftLoginPacket(Packet(inGamePacket.gamePacketContent))
                        loginPacket.decode()
                        setMinecraftClient(
                            loginPacket.protocol,
                            loginPacket.chainData.map { s: String -> JWSObject.parse(s) },
                            "loginPacket.skinData"
                        )

                        if(clientPeer!!.status == PlayerStatus.LoadingGame) {
                            //TODO add encryption to payload
                            //play status ID 0x02, success status 0x00 we send status
                            val playStatusPacket = GamePacket()
                            val contentPlayStatus = PlayStatusPacket(0)
                            contentPlayStatus.encode()
                            playStatusPacket.gamePacketId = MinecraftPacketConstants.PLAY_STATUS
                            playStatusPacket.gamePacketContent = contentPlayStatus.array()!!
                            playStatusPacket.encode()

                            sendMessage(Reliability.UNRELIABLE, 0 , playStatusPacket)
                            //now lets send the resource packet info
                        // GamePacket.create(1, MinecraftPacketConstants.SERVER_TO_CLIENT_HANDSHAKE, ByteArray(0))
                            //sendMessage(Reliability.UNRELIABLE,0, response)
                            ResourcePackInfoHandler(this@RakNetClientPeer)

                        }
                    }
                    MinecraftPacketConstants.CLIENT_TO_SERVER_HANDSHAKE -> { PlayStatusHandler(0, this@RakNetClientPeer) }//todo last check before letting them join
                    MinecraftPacketConstants.RESOURCE_PACK_RESPONSE -> { println("resource pack response")}
                    MinecraftPacketConstants.MALFORM_PACKET -> {println("malform")}
                    else -> throw IllegalArgumentException("Unknown packet sent to factory.")
                }
            } finally {
                buf.release()
            }
        }
    }
}
