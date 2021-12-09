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
import bedrockDragon.network.raknet.protocol.game.entity.MobEquipmentPacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerClosePacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerOpenPacket
import bedrockDragon.network.raknet.protocol.game.inventory.InventoryTransactionPacket
import bedrockDragon.network.raknet.protocol.game.player.InteractPacket
import bedrockDragon.network.raknet.protocol.game.player.MovePlayerPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerActionPacket
import bedrockDragon.network.raknet.protocol.game.player.PlayerAttributePacket
import bedrockDragon.network.raknet.protocol.game.util.TextPacket
import bedrockDragon.network.raknet.protocol.game.world.*
import bedrockDragon.reactive.ISubscriber
import bedrockDragon.reactive.ReactivePacket
import bedrockDragon.reactive.type.ReactivePacket
import bedrockDragon.world.*
import bedrockDragon.world.chunk.Chunk
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.*
import kotlinx.serialization.modules.EmptySerializersModule
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * RaknetClientPeer.MinecraftClientPeer manages player and handles packet/netty
 * implementations. Player is more of a data class that represents the users current
 * status.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Player(override var uuid: String): Living(), ISubscriber {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val logger = KotlinLogging.logger {}

    init {
        read()
    }

    var isConfirmed = false
    //Outgoing Packets
    val nettyQueue = ConcurrentLinkedQueue<MinecraftPacket>()

    var name = ""
    val runtimeEntityId: ULong = /*UUID.randomUUID().mostSignificantBits.toULong()*/ 1u
    val entityIdSelf: Long = /*runtimeEntityId.toLong()*/ 1

    var gamemode = Gamemode.CREATIVE
    var isOp = false

    var world = World.tempDefault //Todo shared world
    var dimension = Dimension.Overworld
    val inventory = PlayerInventory()

    var skinData: Skin? = null

    val adventureSettings = AdventureSettings()
    var chunkRelay = world.getOrLoadRelay(position)

    /**
     * [playInit] is called as soon as the Player is fully connected and joined the game.
     */
    fun playInit() {
        //NBT exists for player if so use those settings else use default and create one

        //register to Chat Rail
        ChatRail.DEFAULT.subscribe(this)
        chunkRelay.addPlayer(this)

        val attribute = PlayerAttributePacket()
        attribute.runtimeEntityId = runtimeEntityId.toLong()
        nettyQueue.add(attribute.gamePacket())

        val publisherUpdate = NetworkChunkPublisherPacket()
        publisherUpdate.position = position
        publisherUpdate.radius = 4
        nettyQueue.add(publisherUpdate.gamePacket())

        sendAdventure()
        sendAttributes()
    }

    override fun getDrops(): List<Item> {
        return emptyList()

    }

    /**
     * [sendAttributes] sends every player attribute(health, hunger, ...) to the client.
     */
    fun sendAttributes() {
        val attributePacket = PlayerAttributePacket()
        nettyQueue.add(attributePacket.gamePacket())
    }

    /**
     * [sendAdventure]
     */
    fun sendAdventure() {
        val packet = AdventureSettingsPacket()

        for(type in AdventureSettings.Type.values()) {
            packet.flag(type.id, type.defaultValue)
        }


        packet.commandPermission = 1
        packet.permissionLevel = 2

        packet.userId = entityIdSelf

        nettyQueue.add(packet.gamePacket())
    }

    //todo review
    override fun tick() {

    }

    override fun armor(): ArmorInventory {
        return ArmorInventory()
    }

    /**
     * [kill] does multiple things:
     * 1. sends the deathscreen to the player.
     * 2. clears inventory, attributes.
     * 3. sends event.
     * 4. respawns player.
     */
    override fun kill() {
        //respawn remove inventory
        //reset attributes to default
        //respawn
        inventory.clear()
    }

    /**
     * [sendChunk] will send a chunk to the player
     */
    fun sendChunk(chunk: Chunk) {
        nettyQueue.add(LevelChunkPacket(chunk).gamePacket())
    }

    enum class Gamemode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }

    /**
     * [sendMessage] sends text as raw data to the client.
     */
    fun sendMessage(text: String, type: Int = 0) {
        val messagePacket = TextPacket()
        messagePacket.type = 0
        messagePacket.needsTranslate = false
        messagePacket.message = text
        nettyQueue.add(messagePacket.gamePacket())
    }

    /**
     * [transfer] will switch a clients worlds used for things like nether portals.
     * @since TBA
     */
    fun transfer(world: World, position: Float3) {

    }

    /**
     * [teleport] moves a player with a loading screen if needed.
     */
    fun teleport(position: Float3) {
        this.position = position
       // nettyQueue.add()
    }

    //todo review
    fun updateAttributes() {

    }

    /**
     * [updateGamemode] sets the player gamemode internaly and also informs the client (adventure settings).
     */
    fun updateGamemode() {

    }

    /**
     * [updateOp] sets the player op privlages internaly and also informs the client (adventure settings).
     */
    fun updateOp(boolean: Boolean) {

    }

    /**
     * [disconnect] will both safely deregister the client and also tell the client that the connection
     * has been terminated.
     */
    fun disconnect(kickMessage: String?) {
       // chunkRelay.removePlayer(this)
    }

    //todo review
    fun emitReactiveCommand(reactivePacket: ReactivePacket<*>) {
        if(reactivePacket.sender != this) {

        }
    }

    /**
     * [openInventory] opens the inventory UI on the client.
     */
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
                logger.info { "$name has disconnected"  }
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

               // chunkRelay.invoke(bedrockDragon.reactive.MovePlayer(movePlayerPacket, this))
            }
            MinecraftPacketConstants.RIDER_JUMP -> { println("RIDER_JUMP") }
            MinecraftPacketConstants.TICK_SYNC -> { println("TICK_SYNC") }
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_ONE -> { /*reject*/ }
            MinecraftPacketConstants.ENTITY_EVENT -> { println("ENTITY_EVENT") }
            MinecraftPacketConstants.INVENTORY_TRANSACTION -> {
                val inventoryTransactionPacket = InventoryTransactionPacket()
                inventoryTransactionPacket.decode(inGamePacket.payload)
                println(inventoryTransactionPacket)
            }
            MinecraftPacketConstants.MOB_EQUIPMENT -> {
                val mobEquipmentPacket = MobEquipmentPacket()
                mobEquipmentPacket.decode(inGamePacket.payload)
                println(mobEquipmentPacket)
            }
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
            MinecraftPacketConstants.BLOCK_PICK_REQUEST -> {
                val blockPickRequestPacket = BlockPickRequestPacket()
                blockPickRequestPacket.decode(inGamePacket.payload)
                println(blockPickRequestPacket)
            }
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
            MinecraftPacketConstants.CONTAINER_CLOSE -> {
                val containerRequest = ContainerClosePacket()
                containerRequest.decode(inGamePacket.payload)
                nettyQueue.add(containerRequest.gamePacket())

            }
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
            MinecraftPacketConstants.SUB_CLIENT_LOGIN -> { println("SUB_CLIENT_LOGIN") }
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
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_TWO -> {/*reject*/}
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_THREE -> { /*reject*/ }
            MinecraftPacketConstants.CLIENT_CACHE_STATUS -> { println("CLIENT_CACHE_STATUS") }
            MinecraftPacketConstants.FILTER_TEXT -> { println("FILTER_TEXT") }
            MinecraftPacketConstants.MALFORM_PACKET -> { MalformHandler(inGamePacket.payload) }
        }
    }

    /**
     * [save] writes all nbt of the player to disk.
     */
    fun save() {
        val playerFile = File("players/$uuid.nbt")
        //playerFile.createNewFile() //only creates if does not exist
        val nbt = Nbt {
            variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
            compression = NbtCompression.Gzip // None, Gzip, Zlib
            compressionLevel = null // in 0..9
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule = EmptySerializersModule
        }
        val compound: NbtCompound = buildNbtCompound("") {  save(this) }

        playerFile.outputStream().use { output ->
            nbt.encodeToStream(compound, output)
        }
    }


    /**
     * [handleChunkRadius] is called when the client messages its desired view distance.
     * we either have to match or be less then what they ask if we go over it would crash the client.
     */
    private fun handleChunkRadius() {
        logger.trace { "sent '$name' update chunk radius" }
        nettyQueue.add(ChunkRadiusUpdatePacket(8).gamePacket())
    }

    /**
     * [filter] for players makes sure that the sender wasnt themselves as thats pointless.
     */
    override fun filter(reactivePacket: ReactivePacket<*>): Boolean {
        return reactivePacket.sender != this
    }
}
