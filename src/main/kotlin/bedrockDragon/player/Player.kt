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
 * Copyright (c) 2021-2022 Nathan Swanson
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

import bedrockDragon.chat.ChatRail
import bedrockDragon.command.CommandEngine
import bedrockDragon.entity.DataTag
import bedrockDragon.entity.ItemEntity
import bedrockDragon.entity.living.Living
import bedrockDragon.inventory.Inventory
import bedrockDragon.inventory.PlayerInventory
import bedrockDragon.item.Item
import bedrockDragon.network.raknet.MetaTag
import bedrockDragon.network.raknet.handler.minecraft.MalformHandler
import bedrockDragon.network.raknet.protocol.game.MinecraftPacket
import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.command.AvailableCommandsPacket
import bedrockDragon.network.raknet.protocol.game.command.CommandRequestPacket
import bedrockDragon.network.raknet.protocol.game.connect.CreativeContentPacket
import bedrockDragon.network.raknet.protocol.game.connect.DisconnectPacket
import bedrockDragon.network.raknet.protocol.game.entity.EntityDataPacket
import bedrockDragon.network.raknet.protocol.game.entity.MobEquipmentPacket
import bedrockDragon.network.raknet.protocol.game.entity.MoveEntityAbsolute
import bedrockDragon.network.raknet.protocol.game.event.EntityEventPacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerClosePacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerOpenPacket
import bedrockDragon.network.raknet.protocol.game.inventory.InventoryTransactionPacket
import bedrockDragon.network.raknet.protocol.game.player.*
import bedrockDragon.network.raknet.protocol.game.type.AttributeBR
import bedrockDragon.network.raknet.protocol.game.ui.TextPacket
import bedrockDragon.network.raknet.protocol.game.world.*
import bedrockDragon.reactive.*
import bedrockDragon.registry.Registry
import bedrockDragon.resource.ServerProperties
import bedrockDragon.util.aabb.AABB
import bedrockDragon.util.text.*
import bedrockDragon.world.World
import bedrockDragon.world.chunk.Chunk
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sqrt

/**
 * RaknetClientPeer.MinecraftClientPeer manages player and handles packet/netty
 * implementations. Player is more of a data class that represents the users current
 * status.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Player(var username: String, override var uuid: String): Living("minecraft:player"), ISubscriber {
    val logger = KotlinLogging.logger {}

    init {
        read() //read nbt and load into fields
        boundingBox = AABB(0.9f,1.9f,0.9f)

        attributes.put(DataTag.DATA_NAMETAG, MetaTag.TypedDefineTag.TAGSTRING(username))

    }

    //Has player sent confirmation packet.
    var isConfirmed = false
    //Outgoing Packets.
    val nettyQueue = ConcurrentLinkedQueue<MinecraftPacket>()
    //entityIdSelf is for players special id. Set to just runtimeId.
    private val entityIdSelf = runtimeEntityId
    //Gamemode for player.
    var gamemode = Gamemode.SURVIVAL
        set(value) {
            if(value == Gamemode.CREATIVE)
                nettyQueue.add(CreativeContentPacket().gamePacketBlocking())
            //send gamemodepacket
            val gamemodePacket = PlayerGameTypePacket()
            gamemodePacket.gamemode = value.ordinal
            nettyQueue.add(gamemodePacket.gamePacketBlocking())
            field = value
        }
    //Operator status.
    var op = false
    //Player armor, hot-bar, and 27 slot inventory.
    val inventory = PlayerInventory()
    //Id that is assigned to an Inventory run addWindow to register new inventory.
    val windowId = ConcurrentHashMap<Inventory, Int>()
    //Player skin data.
    var skinData: Skin? = null
    //chunks render distance
    var renderDistance = Integer.parseInt(ServerProperties.getOrDefault("view-distance", 4) as String)
    //var renderDistance = 8

    var sprinting = false

    var blockMining = Float3(0f,0f,0f)
    //coroutine
    private var epoch = 0

    /* NBT ENABLED VAR*/
    override var health: Float = 20f
        set(value) {
            updateAttribute(value, AttributeBR[4])
            field = value
        }
    //Hunger bar for player.
    private var foodLevel: Float = 20f
        set(value) {
            //update client food level
            updateAttribute(value, AttributeBR[7])
            field = value
        }
    //Value left before food drops.
    private var foodExhaustionLevel: Float = 0f
        set(value) {
            field = value
            if(field >= 4) {
                field = 0f
                if(foodSaturationLevel > 0)
                    foodSaturationLevel--
                else
                    foodLevel--
            }
        }
    //Food Saturation left before using foodLevel.
    var foodSaturationLevel: Float = 20f

    /**
     * [playInit] is called as soon as the Player is fully connected and joined the game.
     */
    fun playInit() {
        //NBT exists for player if so use those settings else use default and create one

        //register to Chat Rail
        ChatRail.DEFAULT.subscribe(this)
        chunkRelay.addPlayer(this)

        sendAdventure()
        sendAttributes()
        sendMeta()

        loadDefaultInventories()

        updateAttribute(0.1f, AttributeBR[5])
        //debug
        PlayerAttributePacket().let {
            it.runtimeEntityId = runtimeEntityId
            it.attributes.addAll(arrayOf(AttributeBR[4], AttributeBR[7], AttributeBR[5], AttributeBR[9], AttributeBR[10]))

            nettyQueue.add(it.gamePacketBlocking())
        }
        //end debug
        ChatRail.DEFAULT.invoke(TextPacket.richTextPacket("$username has joined the server.".YELLOW()))
        //nettyQueue.add(CreativeContentPacket().gamePacket())

    }

    /**
     * [addItem] add given item and send the packet contents. this should use slot change packet.
     */
    fun addItem(item: Item) {
        inventory.addItem(item)
        inventory.sendPacketContents(this)
    }

    private fun loadDefaultInventories() {
        addWindow(inventory, 0, isPermanent = true, isAlwaysOpen = true)

        inventory.sendPacketContents(this)
    }

    override fun damage(amount: Float) {
        super.damage(amount)
        EntityEventPacket().let {
            it.runtimeEntityId = runtimeEntityId
            it.eventId = 2
            nettyQueue.add(it.gamePacketBlocking())
        }
    }

    fun updateChunkPublisherPosition() {
        val publisherUpdate = NetworkChunkPublisherPacket()
        publisherUpdate.position = position
        publisherUpdate.radius = ServerProperties.getProperty("view-distance").toInt()
        nettyQueue.add(publisherUpdate.gamePacketBlocking())
    }
    /**
     * [sendAttributes] sends every player attribute(health, hunger, ...) to the client.
     */
    private fun sendAttributes() {
        val attributePacket = PlayerAttributePacket()
        for (attribute in AttributeBR.attributes) {
            attributePacket.attributes.add(attribute)
        }
        nettyQueue.add(attributePacket.gamePacketBlocking())
    }



    /**
     * [sendCommands] will allow a player to search for commands client side.
     */
    private fun sendCommands() {
        AvailableCommandsPacket().let {

        }
    }
    /**
     * [sendAdventure] Sends the players current permissions on the server.
     */
    private fun sendAdventure() {
        val packet = AdventureSettingsPacket()

        for(type in AdventureSettings.Type.values()) {
            packet.flag(type.id, type.defaultValue)
        }
        packet.commandPermission = 1
        packet.permissionLevel = 2
        packet.customStoredPermissions = 0
        packet.userId = runtimeEntityId

        nettyQueue.add(packet.gamePacketBlocking())
    }

    override fun update() {
        super.update()
        epoch++
        //sprinting
        if(sprinting) {
            foodExhaustionLevel += (sqrt(velocity.x*velocity.x + velocity.z * velocity.z)) * 0.1f
        }

        //fast regen

        if(health < 20f && foodLevel > 9 &&  epoch % 20 == 0) {
            health++
            foodExhaustionLevel+=6
        }
    }
    /**
     * [getDrops] getDrops main function is to get items to drop when entity dies.
     */
//    fun getDrops(): List<Item> {
//        return inventory.getContents().filterNotNull()
//    }

    /**
     * [addWindow] adds a new inventory window into the players registry.
     */
    private fun addWindow(inventory: Inventory, optionalId: Int = -1, isPermanent: Boolean = false, isAlwaysOpen: Boolean = false): Int {
        if(windowId.contains(inventory)) {
            return windowId[inventory]!!
        }



        val newWinId = if(optionalId == -1) windowId.size else optionalId
        windowId[inventory] = newWinId


        if(inventory.isOpenedBy(this)) {
            return newWinId
        } else if (!isAlwaysOpen) {
            //todo make sure not deleted by wipe
        } else {
            inventory.addViewer(this)

            return -1
        }

        return newWinId
    }


    /**
     * [armor] returns the ArmorInventory object of the player.
     */
//    override fun armor(): ArmorInventory {
//        return ArmorInventory()
//    }

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
        health = 0f

        RespawnPacket().let {
            it.position = Float3(0f,90f,0f)
            it.runtimeEntityId = runtimeEntityId
            nettyQueue.add(it.gamePacketBlocking())
        }
        inventory.clear()
        inventory.sendPacketContents(this)

    }

    /**
     * Meta controls gravity, size, air, total air ...
     */
    private fun sendMeta() {
        val entityDataPacket = EntityDataPacket()
        entityDataPacket.runtimeEntityId = runtimeEntityId
        entityDataPacket.tick = 0
        entityDataPacket.metaTag = attributes

        nettyQueue.add(entityDataPacket.gamePacketBlocking())
    }

    /**
     * [sendChunk] will send a chunk to the player
     */
    suspend fun sendChunk(chunk: Chunk) {
        nettyQueue.add(LevelChunkPacket(chunk, chunk.encodePayload()).gamePacket())
    }

    private fun updateAttribute(value: Float, attribute: AttributeBR.Attribute) {
        val attributeInstance = attribute.copy()
        attributeInstance.value = value
        PlayerAttributePacket().let {
            it.runtimeEntityId = runtimeEntityId
            it.attributes.add(attributeInstance)
            nettyQueue.add(it.gamePacketBlocking())
        }
    }

    /**
     * [sendMessage] sends string as raw data to the client.
     */
    fun sendMessage(text: TextPacket) {
        nettyQueue.add(text.gamePacketBlocking())
    }

    fun sendMessage(text: MineText)
    {
        sendMessage(text.minecraft())
    }

    fun sendMessage(text: String, type: Int) {
        sendMessage(TextPacket().let {
            it.message = text
            it.type = type.toByte()
            it.needsTranslate = false
            it
        })
    }

    /**
     * [sendMessage] sends text as raw data to the client.
     */
    fun sendMessage(text: Any, type: Int = 0) {
        sendMessage(text.toString(), type)
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
        MoveEntityAbsolute().let {
            it.positon = position
            it.teleport = true
            it.runtimeEntityId = runtimeEntityId
            nettyQueue.add(it.gamePacketBlocking())
        }
        this.position = position
        runBlocking { chunkRelay.removePlayer(this@Player) }
        chunkRelay = world.getOrLoadRelay(position)
        chunkRelay.addPlayer(this)
        println(this.position)
    }

    /**
     * [disconnect] will both safely deregister the client and also tell the client that the connection
     * has been terminated.
     */
    fun disconnect(kickMessage: String?) {
     //   save() todo
        println("Disconnecting Player: $username")
        nettyQueue.add(DisconnectPacket().let {
            it.kickMessage = kickMessage
            it.gamePacketBlocking()
        })
        runBlocking { chunkRelay.removePlayer(this@Player) }
    }



    //todo review
    fun emitReactiveCommand(reactivePacket: ReactivePacket<*>) {
        if(reactivePacket.filter(this)) {
            nettyQueue.add(reactivePacket.payload.gamePacketBlocking())
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
        containerOpenPacket.windowId = inventory.windowId
        nettyQueue.add(containerOpenPacket.gamePacketBlocking())
    }

    fun emitSound(player: Player, pos: Float3) {

    }

    override suspend fun showFor(players: List<Player>) {
        if(players.isNotEmpty()) {
            val packet = AddPlayerPacket().let {
                it.position = position
                it.gamemode = gamemode.ordinal
                it.runtimeEntityId = runtimeEntityId
                it.username = username
                it.entitySelfId = runtimeEntityId
                it.rotation = rotation
                it.velocity = velocity
                it.metaTag = attributes
                it.uuid = UUID.fromString(uuid)
                it.heldItem = Registry.ITEM_REGISTRY["minecraft:stone"]!!
                it.gamePacket()
            }

            players.forEach { player ->
                player.nettyQueue.add(packet)
            }
        }
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
                sendAdventure()
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
            MinecraftPacketConstants.LEVEL_SOUND_EVENT_ONE -> { /*reject*/ }
            MinecraftPacketConstants.ENTITY_EVENT -> { println("ENTITY_EVENT") }
            MinecraftPacketConstants.INVENTORY_TRANSACTION -> {
                val inventoryTransactionPacket = InventoryTransactionPacket()
                inventoryTransactionPacket.decode(inGamePacket.payload)
            }
            MinecraftPacketConstants.MOB_EQUIPMENT -> {
                val mobEquipmentPacket = MobEquipmentPacket()
                mobEquipmentPacket.decode(inGamePacket.payload)
            }
            MinecraftPacketConstants.MOB_ARMOR_EQUIPMENT -> { println("MOB_ARMOR_EQUIPMENT") }
            MinecraftPacketConstants.INTERACT -> {
                val interact = InteractPacket()
                interact.decode(inGamePacket.payload)
                logger.info { interact }
                when(interact.actionId.toInt()) {
                    4 -> {

                    }
                    6 -> {
                        openInventory(inventory)
                    }
                }

            }
            MinecraftPacketConstants.BLOCK_PICK_REQUEST -> {
                val blockPickRequestPacket = BlockPickRequestPacket()
                blockPickRequestPacket.decode(inGamePacket.payload)
                sendMessage(blockPickRequestPacket.position)
                if(gamemode == Gamemode.SURVIVAL) {
                    sendMessage(world.getBlockAt(blockPickRequestPacket.position).name)
                    val itemEntity = ItemEntity(world.getBlockAt(blockPickRequestPacket.position).asItem())
                    itemEntity.position = blockPickRequestPacket.position + Float3(0.5f,1f,0.5f)
                    runBlocking { chunkRelay.addEntity(itemEntity) }
                    //world.getBlockAt(blockPickRequestPacket.position).asItem().dropItem(this, blockPickRequestPacket.position + Float3(0f,1f,0f), Float3(0f,0f,0f))
                    //inventory.sendPacketContents(this)
                }
            }
            MinecraftPacketConstants.PLAYER_AUTH_INPUT -> {
                println("test")}
            MinecraftPacketConstants.ENTITY_PICK_REQUEST -> { println("ENTITY_PICK_REQUEST") }
            MinecraftPacketConstants.PLAYER_ACTION -> {
                val actionPacket = PlayerActionPacket()
                actionPacket.decode(inGamePacket.payload)
                //action type switch
                when(actionPacket.action) {
                    PlayerActionPacket.ACTION_START_BREAK -> {
                        println("start break")
                    }
                    PlayerActionPacket.ACTION_CONTINUE_BREAK -> {
                        blockMining = actionPacket.coord
                    }
                    PlayerActionPacket.ACTION_STOP_BREAK -> {
                        chunkRelay.invoke(BreakBlock(actionPacket, this))
//                        val levelEventPacket = LevelEventPacket()
//                        val block = world.getBlockAt(actionPacket.coord)
//                        levelEventPacket.eventId = EVENT_PARTICLE_DESTROY
//                        levelEventPacket.position = actionPacket.coord
//                        levelEventPacket.data = 0 //todo block type
//                        nettyQueue.add(levelEventPacket.gamePacket())
                    }
                    PlayerActionPacket.ACTION_JUMP -> {
                        foodExhaustionLevel += if(sprinting) 0.2f else 0.05f
                        sendMeta()
                    }
                    PlayerActionPacket.ACTION_START_SPRINT -> {
                        sprinting = true
                    }
                    PlayerActionPacket.ACTION_STOP_SPRINT -> {
                        sprinting = false
                    }
                    PlayerActionPacket.ACTION_DROP_ITEM -> {

                    }
                    PlayerActionPacket.ACTION_STOP_SNEAK,
                    PlayerActionPacket.ACTION_START_SNEAK -> {
                        chunkRelay.invoke(Sneak(inGamePacket.payload as PlayerActionPacket, this))
                    }
                }
            }
            MinecraftPacketConstants.ENTITY_FALL -> { println("ENTITY_FALL") }
            MinecraftPacketConstants.SET_ENTITY_DATA -> { println("SET_ENTITY_DATA") }
            MinecraftPacketConstants.SET_ENTITY_MOTION -> { println("SET_ENTITY_MOTION") }
            MinecraftPacketConstants.ANIMATE -> {
                chunkRelay.invoke(AnimatePlayer(AnimatePacket().let {
                    it.decode(inGamePacket.payload)
                    it
                }, this))
            }
            MinecraftPacketConstants.RESPAWN -> {
                RespawnPacket().let {
                    it.decode(inGamePacket.payload)
                    it.position = Float3(0f, 100f, 0f)
                    it.state = 1
                    nettyQueue.add(it.gamePacketBlocking())
                }

                health = 20f
            }
            MinecraftPacketConstants.CONTAINER_CLOSE -> {
                val containerRequest = ContainerClosePacket()
                containerRequest.decode(inGamePacket.payload)
                nettyQueue.add(containerRequest.gamePacketBlocking())

            }
            MinecraftPacketConstants.PLAYER_HOTBAR -> { println("PLAYER_HOTBAR") }
            MinecraftPacketConstants.INVENTORY_CONTENT -> { println("INVENTORY_CONTENT") }
            MinecraftPacketConstants.INVENTORY_SLOT -> { println("INVENTORY_SLOT") }
            MinecraftPacketConstants.CRAFTING_EVENT -> { println("CRAFTING_EVENT") }
            MinecraftPacketConstants.ADVENTURE_SETTINGS -> {
//                val adventurePacket = AdventureSettingsPacket()
//                adventurePacket.decode(inGamePacket.payload)
//                logger.info { adventurePacket.toString() }
//                nettyQueue.add(adventurePacket.gamePacket())
                println("adventure")
            }
            MinecraftPacketConstants.PLAYER_INPUT -> { println("PLAYER_INPUT") }
            MinecraftPacketConstants.SET_PLAYER_GAME_TYPE -> { /*reject*/ }
            MinecraftPacketConstants.MAP_INFO_REQUEST -> { println("MAP_INFO_REQUEST") }
            MinecraftPacketConstants.REQUEST_CHUNK_RADIUS -> {
                handleChunkRadius(RequestChunkRadiusPacket().let {
                    it.decode(inGamePacket.payload)
                    it.chunkRadius
                })
            }
            MinecraftPacketConstants.ITEMFRAME_DROP_ITEM -> { println("ITEMFRAME_DROP_ITEM") }
            MinecraftPacketConstants.COMMAND_REQUEST -> {
                val commandPacket = CommandRequestPacket()
                commandPacket.decode(inGamePacket.payload)
                val commandArgs = commandPacket.command.split(" ")
                Registry.COMMAND_REGISTRY[commandArgs[0]]?.let {
                    //it.invoke?.let { it1 -> it1(this, commandArgs.subList(1,commandArgs.size).toTypedArray()) }
                    CommandEngine.invokeWith(commandArgs.subList(1,commandArgs.size).toTypedArray(), it, this)
                } ?: sendMessage("Unknown command, use /help for a list of commands. ")


            }
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
            MinecraftPacketConstants.MALFORM_PACKET -> {
                println("ERROR PACKET")
                MalformHandler(inGamePacket.payload) }
            else -> {
                println("Unkown Packet ${inGamePacket.packetId}")
            }
        }
    }

    /**
     * [save] writes all nbt of the player to disk.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun save() {
        val playerFile = File("players/$uuid.nbt")
        if(!playerFile.exists())
            playerFile.createNewFile()
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
     * we either have to match or be less than what they ask if we go over it would crash the client.
     */
    private fun handleChunkRadius(max: Int) {
        renderDistance = max.coerceAtMost(ServerProperties["view-distance"] as Int)
        //logger.trace { "sent '$name' update chunk radius $renderDistance " }
        nettyQueue.add(ChunkRadiusUpdatePacket(renderDistance).gamePacketBlocking())
    }

    /**
     * [filter] for players makes sure that the sender wasn't themselves as that's pointless.
     */
    override fun filter(reactivePacket: ReactivePacket<*>): Boolean {
        return reactivePacket.sender != this
    }

    enum class Gamemode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }
}