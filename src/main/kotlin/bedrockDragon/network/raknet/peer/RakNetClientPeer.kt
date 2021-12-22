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
import bedrockDragon.network.raknet.handler.PacketConstants
import bedrockDragon.network.raknet.handler.login.ClientToServerHandler
import bedrockDragon.network.raknet.handler.minecraft.*
import bedrockDragon.network.raknet.protocol.RaknetConnectionStatus
import bedrockDragon.network.raknet.protocol.ConnectionType
import bedrockDragon.network.raknet.protocol.Reliability
import bedrockDragon.network.raknet.protocol.game.*
import bedrockDragon.network.raknet.protocol.game.connect.BiomeDefinitionPacket
import bedrockDragon.network.raknet.protocol.game.connect.CreativeContentPacket
import bedrockDragon.network.raknet.protocol.game.connect.PlayStatusPacket
import bedrockDragon.network.raknet.protocol.game.connect.StartGamePacket
import bedrockDragon.network.raknet.protocol.game.entity.AvaliableEntityIDPacket
import bedrockDragon.network.raknet.protocol.game.entity.EntityDataPacket
import bedrockDragon.network.raknet.protocol.game.login.MinecraftLoginPacket
import bedrockDragon.network.raknet.protocol.game.world.SetTimePacket
import bedrockDragon.network.raknet.protocol.message.EncapsulatedPacket
import bedrockDragon.network.zlib.PacketCompression
import bedrockDragon.player.Player
import com.nimbusds.jose.JWSObject
import io.netty.channel.Channel
import it.unimi.dsi.fastutil.io.FastBufferedInputStream
import org.jetbrains.annotations.Nullable
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress

/**
 * RaknetClientPeer is the bridge between client -> [raknet -> minecraftPeer -> player] -> reactiveX
 * @author Nathan Swanson
 * @since ALPHA
 */
class RakNetClientPeer(val server: DragonServer, connectionType: ConnectionType, guid: Long, maximumTransferUnit: Int, channel: Channel, val sender: InetSocketAddress)
    : RakNetPeer(sender, guid, maximumTransferUnit, connectionType, channel) {

    var status: RaknetConnectionStatus = RaknetConnectionStatus.DISCONNECTED
    private var clientPeer : MinecraftClientPeer? = null
   // var cacheEnabled = false

    fun destroy() {
        clientPeer!!.player!!.save()
    }

    fun attemptMinecraftHandoff() {
        clientPeer!!.fireJoinSequence()
    }

    fun setMinecraftClient(protocol: Int, chainData: List<JWSObject>, skinData: String) {
        clientPeer = MinecraftClientPeer(protocol,chainData,skinData)
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

    override fun updateServer() {
        if (clientPeer != null) {
            if(clientPeer!!.status == PlayerStatus.InGame) {
                if(clientPeer?.player?.nettyQueue!!.isNotEmpty()) {
                    for(gamePacket in clientPeer?.player?.nettyQueue!!) {
                        sendMessage(gamePacket.reliability, 0, gamePacket)
                        clientPeer!!.player!!.nettyQueue.remove(gamePacket)
                    }
                }
            }
        }
    }

    private fun bedrockClient(): MinecraftClientPeer? {
        return clientPeer
    }

    private inner class MinecraftClientPeer(val protocol: Int, val playerData: List<JWSObject>, val skinData: String): MinecraftPeer() {
        var xuid: Long = 0
        var uuid: String = ""
        var userName: String = ""
        var status = PlayerStatus.Connected
        var player: Player? = null
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

        fun fireJoinSequence() {
            player = Player(uuid)

            //println("Entity Identifier packet")

            sendMessage(Reliability.RELIABLE_ORDERED, 0, StartGamePacket.capture(player!!).gamePacket())

            sendMessage(Reliability.RELIABLE_ORDERED, 0 , BiomeDefinitionPacket().gamePacket())

            sendMessage(Reliability.RELIABLE_ORDERED, 0, AvaliableEntityIDPacket().gamePacket())

            sendMessage(Reliability.RELIABLE_ORDERED, 0 , CreativeContentPacket().gamePacket())




            //send one chunk through netty then say play status good
            //debug chunk

            //val entityDataPacket = EntityDataPacket()
            //entityDataPacket.runtimeEntityId = player!!.runtimeEntityId.toLong()
            //sendMessage(Reliability.UNRELIABLE, 0, entityDataPacket.gamePacket())

            sendMessage(Reliability.UNRELIABLE, 0, PlayStatusPacket(3).gamePacket())

            clientPeer!!.status = PlayerStatus.InGame
            logger.info { "${clientPeer!!.userName} has joined the game." }

            val setTime = SetTimePacket()

            sendMessage(Reliability.UNRELIABLE, 0, setTime.gamePacket())

            player!!.playInit()


        }
    }


    private inner class MinecraftPacketFactory {
        fun createIncomingPacketHandler(@Nullable client: MinecraftPeer?, packet: EncapsulatedPacket) {
            //if InGame go to player packet handle

                val buf = FastBufferedInputStream(packet.payload.inputStream)
                //val buf = packet.payload.buffer()
                try {
                    //val bytes = ByteArray(buf.readableBytes())
                    //buf.readBytes(bytes)
                    //removes zlib compression
                    val decompressed = PacketCompression.decompress(buf.readAllBytes())

                    val inGamePacket = MinecraftPacket()
                    inGamePacket.setBuffer(decompressed)
                    inGamePacket.decode()

                        //if packet is non-reflective send the packet to the observer deck.
                        //packet is converted into dragon protocol which is an Observable
                        //protocol request will then wait its turn to be broadcast by the reactor and
                        // then filtered through the great mesh
                    if(clientPeer != null && inGamePacket.packetId > 8) {
                        if(clientPeer!!.player != null) {
                            clientPeer!!.player!!.handIncomingCommand(inGamePacket)
                        }
                    } else {
                        when (inGamePacket.packetId) {
                            MinecraftPacketConstants.LOGIN -> {

                                val loginPacket = MinecraftLoginPacket()
                                loginPacket.decode(inGamePacket.payload)
                                setMinecraftClient(
                                    loginPacket.protocol,
                                    loginPacket.chainData.map { s: String -> JWSObject.parse(s) },
                                    "loginPacket.skinData"
                                )

                                if (clientPeer!!.status == PlayerStatus.LoadingGame) {
                                    //TODO add encryption to payload

                                    sendMessage(
                                        Reliability.UNRELIABLE,
                                        0,
                                        PlayStatusPacket(0).gamePacket()
                                    )
                                    //now lets send the resource packet info
                                    ResourcePackInfoHandler(this@RakNetClientPeer)
                                    //if no resource packets just send Vanilla
                                    //TODO doesnt handle client blobs yet

                                    ResourcePackStackHandler(this@RakNetClientPeer)

                                }
                            }
                            MinecraftPacketConstants.CLIENT_TO_SERVER_HANDSHAKE -> {
                          //      ClientToServerHandler(inGamePacket.payload)
                                PlayStatusHandler(0, this@RakNetClientPeer)
                            }//todo last check before letting them join
                            //for now this initiates start game packet
                            MinecraftPacketConstants.RESOURCE_PACK_RESPONSE -> {
                                ResourcePackResponseHandler(inGamePacket, this@RakNetClientPeer)
                            }
                            MinecraftPacketConstants.CLIENT_CACHE_STATUS -> {
                                ClientCacheHandler(this@RakNetClientPeer, inGamePacket)
                            }
                            MinecraftPacketConstants.MALFORM_PACKET -> {
                                MalformHandler(inGamePacket.payload)
                            }
                            else -> throw IllegalArgumentException("Unknown packet sent to factory.")
                        }
                    }
                } finally {
                    buf.close()
                }

        }
    }
}
