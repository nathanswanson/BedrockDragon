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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.player

import bedrockDragon.Item
import bedrockDragon.chat.ChatRail
import bedrockDragon.entity.living.Living
import bedrockDragon.inventory.ArmorInventory
import bedrockDragon.inventory.Inventory
import bedrockDragon.inventory.PlayerInventory
import bedrockDragon.network.raknet.handler.minecraft.MalformHandler
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerOpenPacket
import bedrockDragon.network.raknet.protocol.game.player.InteractPacket
import bedrockDragon.network.raknet.protocol.game.player.MovePlayerPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerActionPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerAttributePacket
import bedrockDragon.network.raknet.protocol.game.util.TextPacket
import bedrockDragon.network.raknet.protocol.game.world.AdventureSettingsPacket
import bedrockDragon.network.raknet.protocol.game.world.ChunkRadiusUpdatePacket
import bedrockDragon.network.raknet.protocol.game.world.LevelChunkPacket
import bedrockDragon.network.raknet.protocol.game.world.NetworkChunkPublisherPacket
import bedrockDragon.network.world.WorldInt2
import bedrockDragon.reactive.type.ISubscriber
import bedrockDragon.reactive.type.MovePlayer
import bedrockDragon.reactive.type.ReactivePacket
import bedrockDragon.world.*
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * RaknetClientPeer.MinecraftClientPeer manages player and handles packet/netty
 * implementations. Player is more of a data class that represents the users current
 * status.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Player: Living(), ISubscriber {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val logger = KotlinLogging.logger {}

    var isConfirmed = false
    //Outgoing Packets
    val nettyQueue = ConcurrentLinkedQueue<MinecraftPacket>()

    var name = ""
    val runtimeEntityId: ULong = /*UUID.randomUUID().mostSignificantBits.toULong()*/ 1u
    val entityIdSelf: Long = /*runtimeEntityId.toLong()*/ 1

    var gamemode = Gamemode.SURVIVAL
    var isOp = false

    var position = Float3(1f,38f,1f)
    var rotation = Float2(0f, 0f)
    var world = World.tempDefault //Todo shared world
    var dimension = Dimension.Overworld
    val inventory = PlayerInventory()

    var skinData: Skin? = null

    var chunkRelay = world.getOrLoadRelay(WorldInt2(position.xy))

    fun playInit() {

        //register to Chat Rail
        ChatRail.DEFAULT.subscribe(this)
        chunkRelay.addPlayer(this)


        for(x in 1..15) {
            for(z in 1..15) {
                nettyQueue.add(LevelChunkPacket.emptyChunk(x - 8,z - 8).gamePacket())
            }
        }
        logger.info { "send chunk." }

        val attribute = PlayerAttributePacket()
        attribute.runtimeEntityId = runtimeEntityId.toLong()
        nettyQueue.add(attribute.gamePacket())

        val publisherUpdate = NetworkChunkPublisherPacket()
        publisherUpdate.position = position
        publisherUpdate.radius = 4
        nettyQueue.add(publisherUpdate.gamePacket())
    }

    override fun getDrops(): List<Item> {
        return emptyList()

    }

    override fun getHealth(): Float {
        return 0f
    }

    override fun tick() {
    }

    override fun armor(): ArmorInventory {
        return ArmorInventory()
    }

    override fun damage() {
    }

    enum class Gamemode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }

    fun sendMessage(text: String, type: Int = 0) {
        val messagePacket = TextPacket()
        messagePacket.type = 0
        messagePacket.needsTranslate = false
        messagePacket.message = text
        nettyQueue.add(messagePacket.gamePacket())
    }

    fun transfer(world: World, position: Float3) {

    }

    fun teleport(position: Float3) {
        this.position = position
       // nettyQueue.add()
    }

    fun updateAttributes() {

    }

    fun updateGamemode() {

    }

    fun updateOp(boolean: Boolean) {

    }

    fun disconnect(kickMessage: String?) {
        chunkRelay.removePlayer(this)
    }

    fun emitReactiveCommand(reactivePacket: ReactivePacket<*>) {
        if(reactivePacket.sender != this) {

        }
    }

    fun openInventory(inventory: Inventory) {
        val containerOpenPacket = ContainerOpenPacket()
        containerOpenPacket.type = inventory.type
        containerOpenPacket.position = position
        containerOpenPacket.entityId = entityIdSelf
        containerOpenPacket.windowId = 0
        nettyQueue.add(containerOpenPacket.gamePacket())
    }

    /**
     * Hand incoming command
     * Almost every incoming command is sent to chunk relay as a coroutine however, some packets
     * like malformPacket, disconnect, text, adventure settings, are handled by player.kt
     * @param inGamePacket
     */
    fun handIncomingCommand(inGamePacket: MinecraftPacket) {

        when(inGamePacket.packetId) {
            MinecraftPacketConstants.DISCONNECT -> {
                disconnect(null)
                logger.debug { "$name has disconnected"  }
            }
            MinecraftPacketConstants.TEXT -> {

                val payload = TextPacket()
                payload.decode(inGamePacket.payload)
                ChatRail.DEFAULT.invoke(payload.message)
            }
            MinecraftPacketConstants.MOVE_ENTITY_ABSOLUTE -> { println("MOVE_ENTITY_ABSOLUTE") }
            MinecraftPacketConstants.MOVE_PLAYER -> {
                val movePlayerPacket = MovePlayerPacket()
                movePlayerPacket.decode(inGamePacket.payload)
                position = movePlayerPacket.position

                chunkRelay.invoke(MovePlayer(movePlayerPacket, this))
            }
            MinecraftPacketConstants.RIDER_JUMP -> { println("RIDER_JUMP") }
            MinecraftPacketConstants.TICK_SYNC -> { println("TICK_SYNC") }
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_ONE -> { println("LEVEL_SOUND_EVENT_ONE") }
            MinecraftPacketConstants.ENTITY_EVENT -> { println("ENTITY_EVENT") }
            MinecraftPacketConstants.INVENTORY_TRANSACTION -> { println("INVENTORY_TRANSACTION") }
            MinecraftPacketConstants.MOB_EQUIPMENT -> { println("MOB_EQUIPMENT") }
            MinecraftPacketConstants.MOB_ARMOR_EQUIPMENT -> { println("MOB_ARMOR_EQUIPMENT") }
            MinecraftPacketConstants.INTERACT -> {
                val interact = InteractPacket()
                interact.decode(inGamePacket.payload)
                when(interact.actionId.toInt()) {
                    6 -> {
                        openInventory(inventory)
                    }
                }
               // println(interact.targetRuntimeEntityId)
               // println(interact.actionId)
            }
            MinecraftPacketConstants.BLOCK_PICK_REQUEST -> { println("BLOCK_PICK_REQUEST") }
            MinecraftPacketConstants.ENTITY_PICK_REQUEST -> { println("ENTITY_PICK_REQUEST") }
            MinecraftPacketConstants.PLAYER_ACTION -> {
                val actionPacket = PlayerActionPacket()
                actionPacket.decode(inGamePacket.payload)
               // logger.info { actionPacket.toString() }
            }
            MinecraftPacketConstants.ENTITY_FALL -> { println("ENTITY_FALL") }
            MinecraftPacketConstants.SET_ENTITY_DATA -> { println("SET_ENTITY_DATA") }
            MinecraftPacketConstants.SET_ENTITY_MOTION -> { println("SET_ENTITY_MOTION") }
            MinecraftPacketConstants.ANIMATE -> {

            }
            MinecraftPacketConstants.RESPAWN -> { println("RESPAWN") }
            MinecraftPacketConstants.CONTAINER_CLOSE -> { println("CONTAINER_CLOSE") }
            MinecraftPacketConstants.PLAYER_HOTBAR -> { println("PLAYER_HOTBAR") }
            MinecraftPacketConstants.INVENTORY_CONTENT -> { println("INVENTORY_CONTENT") }
            MinecraftPacketConstants.INVENTORY_SLOT -> { println("INVENTORY_SLOT") }
            MinecraftPacketConstants.CRAFTING_EVENT -> { println("CRAFTING_EVENT") }
            MinecraftPacketConstants.ADVENTURE_SETTINGS -> {
                val adventurePacket = AdventureSettingsPacket()
                adventurePacket.decode(inGamePacket.payload)
                //logger.info { adventurePacket.toString() }
                nettyQueue.add(adventurePacket.gamePacket())
            }
            MinecraftPacketConstants.PLAYER_INPUT -> { println("PLAYER_INPUT") }
            MinecraftPacketConstants.SET_PLAYER_GAME_TYPE -> { println("SET_PLAYER_GAME_TYPE") }
            MinecraftPacketConstants.MAP_INFO_REQUEST -> { println("MAP_INFO_REQUEST") }
            MinecraftPacketConstants.REQUEST_CHUNK_RADIUS -> { handleChunkRadius() }
            MinecraftPacketConstants.ITEMFRAME_DROP_ITEM -> { println("ITEMFRAME_DROP_ITEM") }
            MinecraftPacketConstants.COMMAND_REQUEST -> { println("COMMAND_REQUEST") }
            MinecraftPacketConstants.COMMAND_BLOCK_UPDATE -> { println("COMMAND_BLOCK_UPDATE") }
            MinecraftPacketConstants.RESOURCE_PACK_CHUNK_REQUEST -> { println("RESOURCE_PACK_CHUNK_REQUEST") }
            MinecraftPacketConstants.PURCHASE_RECEIPT -> { println("PURCHASE_RECEIPT") }
            MinecraftPacketConstants.PLAYER_SKIN -> { println("PLAYER_SKIN") }
            MinecraftPacketConstants.SUB_CLIENT_LOGING -> { println("SUB_CLIENT_LOGIN") }
            MinecraftPacketConstants.NPC_REQUEST -> { println("NPC_REQUEST") }
            MinecraftPacketConstants.PHOTO_TRANSFER -> { println("PHOTO_TRANSFER") }
            MinecraftPacketConstants.MODEL_FORM_RESPONSE ->  { println("MODEL_FORM_RESPONSE") }
            MinecraftPacketConstants.SERVER_SETTINGS_REQUEST -> { println("SERVER_SETTINGS_REQUEST") }
            MinecraftPacketConstants.LAB_TABLE -> { println("LAB_TABLE") }
            MinecraftPacketConstants.SET_LOCAL_PLAYER_AS_INITIALIZED -> {
                isConfirmed = true
            }
            MinecraftPacketConstants.NETWORK_STACK_LATENCY -> { println("NETWORK_STACK_LATENCY") }
            MinecraftPacketConstants.SCRIPT_CUSTOM_EVENT -> { println("SCRIPT_CUSTOM_EVENT") }
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_TWO -> { println("LEVEL_SOUND_EVENT_TWO") }
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_THREE -> { println("LEVEL_SOUND_EVENT_THREE") }
            MinecraftPacketConstants.CLIENT_CACHE_STATUS -> { println("CLIENT_CACHE_STATUS") }
            MinecraftPacketConstants.FILTER_TEXT -> { println("FILTER_TEXT") }
            MinecraftPacketConstants.MALFORM_PACKET -> { MalformHandler(inGamePacket.payload) }
        }
    }

    private fun handleChunkRadius() {
        logger.trace { "sent '$name' update chunk radius" }
        nettyQueue.add(ChunkRadiusUpdatePacket(8).gamePacket())
    }

    override fun filter(reactivePacket: ReactivePacket<*>): Boolean {
        return reactivePacket.sender != this
    }
}