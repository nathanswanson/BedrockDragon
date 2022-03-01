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
import bedrockDragon.entity.DataTag.DATA_AIR
import bedrockDragon.entity.DataTag.DATA_BOUNDING_BOX_HEIGHT
import bedrockDragon.entity.DataTag.DATA_BOUNDING_BOX_WIDTH
import bedrockDragon.entity.DataTag.DATA_COLOR
import bedrockDragon.entity.DataTag.DATA_FLAGS
import bedrockDragon.entity.DataTag.DATA_FLAG_ALWAYS_SHOW_NAMETAG
import bedrockDragon.entity.DataTag.DATA_FLAG_BREATHING
import bedrockDragon.entity.DataTag.DATA_FLAG_GRAVITY
import bedrockDragon.entity.DataTag.DATA_FLAG_HAS_COLLISION
import bedrockDragon.entity.DataTag.DATA_HEALTH
import bedrockDragon.entity.DataTag.DATA_LEAD_HOLDER_EID
import bedrockDragon.entity.DataTag.DATA_MAX_AIR
import bedrockDragon.entity.DataTag.DATA_NAMETAG
import bedrockDragon.entity.DataTag.DATA_SCALE
import bedrockDragon.entity.living.Living
import bedrockDragon.inventory.ArmorInventory
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
import bedrockDragon.network.raknet.protocol.game.event.LevelEventPacket
import bedrockDragon.network.raknet.protocol.game.event.LevelEventPacket.Companion.EVENT_BLOCK_START_BREAK
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerClosePacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerOpenPacket
import bedrockDragon.network.raknet.protocol.game.inventory.InventoryTransactionPacket
import bedrockDragon.network.raknet.protocol.game.player.*
import bedrockDragon.network.raknet.protocol.game.type.AttributeBR
import bedrockDragon.network.raknet.protocol.game.ui.TextPacket
import bedrockDragon.network.raknet.protocol.game.world.*
import bedrockDragon.reactive.ISubscriber
import bedrockDragon.reactive.ReactivePacket
import bedrockDragon.registry.Registry
import bedrockDragon.resource.ServerProperties
import bedrockDragon.util.WorldInt2
import bedrockDragon.world.PaletteGlobal
import bedrockDragon.world.World
import bedrockDragon.world.chunk.Chunk
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import mu.KotlinLogging
import net.benwoodworth.knbt.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * RaknetClientPeer.MinecraftClientPeer manages player and handles packet/netty
 * implementations. Player is more of a data class that represents the users current
 * status.
 * @author Nathan Swanson
 * @since ALPHA
 */
class Player(override var uuid: String): Living(), ISubscriber {
    val logger = KotlinLogging.logger {}

    init {
        read() //read nbt and load into fields
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
            //send gamemodepacket
            val gamemodePacket = PlayerGameTypePacket()
            gamemodePacket.gamemode = value.ordinal
            nettyQueue.add(gamemodePacket.gamePacket())
            field = value
        }
    //Operator status.
    var op = false
    //World player is on.
    var world = Registry.WORLD_REGISTRY[0]
    //Player armor, hot-bar, and 27 slot inventory.
    val inventory = PlayerInventory()
    //Id that is assigned to an Inventory run addWindow to register new inventory.
    val windowId = ConcurrentHashMap<Inventory, Int>()
    //Player skin data.
    var skinData: Skin? = null
    //Metadata for player. Gravity, food, health.
    private val playerMeta =  MetaTag()
    //chunks render distance
    var renderDistance = 4
    //chunkRelay the player is currently on.
    var chunkRelay = world.getOrLoadRelay(position)
    //temp
    var sendChunkCoord = ArrayList<WorldInt2>()

    /* NBT ENABLED VAR*/
    override var health: Float = 20f
        set(value) {
            updateAttribute(value, AttributeBR[4])
            field = value
        }
    //Hunger bar for player.
    var foodLevel: Byte = 20
        set(value) {
            //update client food level
            updateAttribute(value.toFloat(), AttributeBR[7])
            field = value
        }
    //Value left before food drops.
    var foodExhaustionLevel: Byte = 0
    //Food Saturation left before using foodLevel.
    var foodSaturationLevel: Byte = 0

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

        //updateAttribute(0.1f, AttributeBR[5])
        //debug
        PlayerAttributePacket().let {
            it.runtimeEntityId = runtimeEntityId
            it.attributes.addAll(arrayOf(AttributeBR[4], AttributeBR[7], AttributeBR[5], AttributeBR[9], AttributeBR[10]))

            nettyQueue.add(it.gamePacket())
        }
        //end debug

        nettyQueue.add(CreativeContentPacket().gamePacket())

    }

    /**
     * [addItemToPlayerInventory] add given item and send the packet contents. this should use slot change packet.
     */
    fun addItemToPlayerInventory(item: Item) {
        inventory.addItem(item)
        inventory.sendPacketContents(this)
    }

    private fun loadDefaultInventories() {
        addWindow(inventory, 0, isPermanent = true, isAlwaysOpen = true)

        //val woodenSword = Registry.ITEM_REGISTRY["minecraft:wooden_sword"]
        //woodenSword.iDurability = 20
        //woodenSword.count = 1

        //val diamondPickAxe = Registry.ITEM_REGISTRY["minecraft:diamond_pickaxe"]
        //diamondPickAxe.iDurability = 20
        //diamondPickAxe.count = 1

        //inventory.addItem(diamondPickAxe)

        //inventory.addItem(woodenSword)
        inventory.sendPacketContents(this)
    }

    fun updateChunkPublisherPosition() {
        val publisherUpdate = NetworkChunkPublisherPacket()
        publisherUpdate.position = position
        publisherUpdate.radius = ServerProperties.getProperty("view-distance").toInt()
        nettyQueue.add(publisherUpdate.gamePacket())
    }
    /**
     * [sendAttributes] sends every player attribute(health, hunger, ...) to the client.
     */
    private fun sendAttributes() {
        val attributePacket = PlayerAttributePacket()
        for (attribute in AttributeBR.attributes) {
            attributePacket.attributes.add(attribute)
        }
        nettyQueue.add(attributePacket.gamePacket())
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

        nettyQueue.add(packet.gamePacket())
    }

    /**
     * [getDrops] getDrops main function is to get items to drop when entity dies.
     */
    override fun getDrops(): List<Item> {
        return inventory.getContents().filterNotNull()
    }

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
     * review needed
     */
    override suspend fun tick() {
        while (true) { //20 tps aprox.
            runWithDynamicDelay {
               // checkIfChunkUpdateRequired()
            }
            delay(1000)
        }


    }
    /**
     * review needed
     */
    private fun runWithDynamicDelay(tasks: () -> Unit) {
        tasks.invoke()
    }

    /**
     * [armor] returns the ArmorInventory object of the player.
     */
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
        health = 0f

        RespawnPacket().let {
            it.position = Float3(0f,150f,0f)
            it.runtimeEntityId = runtimeEntityId
            nettyQueue.add(it.gamePacket())
        }
        inventory.clear()
        inventory.sendPacketContents(this)
    }

    /**
     * Meta controls gravity, size, air, total air ...
     */
    private fun sendMeta() {
        //var flag = 0L xor (1L shl DATA_FLAG_GRAVITY)
        //flag = flag xor (1L shl DATA_FLAG_BREATHING)
        //flag = flag xor (1L shl DATA_FLAG_HAS_COLLISION)


        //for testing
        playerMeta.put(DATA_FLAGS, MetaTag.TypedDefineTag.TAGLONG(422246825345024L))
        playerMeta.put(DATA_FLAG_ALWAYS_SHOW_NAMETAG, MetaTag.TypedDefineTag.TAGBYTE(1))
//        playerMeta.put(DATA_COLOR, MetaTag.TypedDefineTag.TAGBYTE(0))
//        playerMeta.put(DATA_AIR, MetaTag.TypedDefineTag.TAGSHORT(400))
//        playerMeta.put(DATA_MAX_AIR, MetaTag.TypedDefineTag.TAGSHORT(400))
//        playerMeta.put(DATA_NAMETAG, MetaTag.TypedDefineTag.TAGSTRING(""))
//        playerMeta.put(DATA_LEAD_HOLDER_EID, MetaTag.TypedDefineTag.TAGLONG(-1L))
//        playerMeta.put(DATA_SCALE, MetaTag.TypedDefineTag.TAGFLOAT(1f))
//        playerMeta.put(DATA_BOUNDING_BOX_WIDTH, MetaTag.TypedDefineTag.TAGFLOAT(1f))
//        playerMeta.put(DATA_BOUNDING_BOX_HEIGHT, MetaTag.TypedDefineTag.TAGFLOAT(2f))
//        playerMeta.put(DATA_HEALTH, MetaTag.TypedDefineTag.TAGINT(health.toInt()))

        val entityDataPacket = EntityDataPacket()
        entityDataPacket.runtimeEntityId = runtimeEntityId
        entityDataPacket.tick = 0 //todo
        entityDataPacket.metaTag = playerMeta

        nettyQueue.add(entityDataPacket.gamePacket())
    }

    /**
     * [sendChunk] will send a chunk to the player
     */
    fun sendChunk(chunk: Chunk) {
        nettyQueue.add(LevelChunkPacket(chunk).gamePacket())
        sendChunkCoord.add(chunk.position)



    }

    private fun updateAttribute(value: Float, attribute: AttributeBR.Attribute) {
        val attributeInstance = attribute.copy()
        attributeInstance.value = value
        PlayerAttributePacket().let {
            it.runtimeEntityId = runtimeEntityId
            it.attributes.add(attributeInstance)
            nettyQueue.add(it.gamePacket())
        }
    }

    /**
     * [sendMessage] sends text as raw data to the client.
     */
    fun sendMessage(text: Any, type: Int = 0) {
        sendMessage(text.toString(), type)
    }

    /**
     * [sendMessage] sends string as raw data to the client.
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
        MoveEntityAbsolute().let {
            it.positon = position
            it.teleport = true
            it.runtimeEntityId = runtimeEntityId
            nettyQueue.add(it.gamePacket())
        }
        this.position = position
        chunkRelay.removePlayer(this)
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
        nettyQueue.add(DisconnectPacket().let {
            it.kickMessage = kickMessage ?: ""
            it.hideDisconnectScreen = kickMessage == null
            it.encode()
            it.gamePacket()
        })
        chunkRelay.removePlayer(this)
    }



    //todo review
    fun emitReactiveCommand(reactivePacket: ReactivePacket<*>) {
//        if(reactivePacket.sender != this) {
//        }
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
                sendAdventure()
                ChatRail.DEFAULT.invoke(payload.message)

            }
            MinecraftPacketConstants.MOVE_ENTITY_ABSOLUTE -> { println("MOVE_ENTITY_ABSOLUTE") }
            MinecraftPacketConstants.MOVE_PLAYER -> {
                val movePlayerPacket = MovePlayerPacket()
                movePlayerPacket.decode(inGamePacket.payload)
                position = movePlayerPacket.position

                chunkRelay.invoke(bedrockDragon.reactive.MovePlayer(movePlayerPacket, this))
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
                    6 -> {
                        openInventory(inventory)
                    }
                }

            }
            MinecraftPacketConstants.BLOCK_PICK_REQUEST -> {
                val blockPickRequestPacket = BlockPickRequestPacket()
                blockPickRequestPacket.decode(inGamePacket.payload)
                sendMessage(blockPickRequestPacket.position)
                if(gamemode == Gamemode.CREATIVE) {
                    sendMessage(world.getBlockAt(blockPickRequestPacket.position).name)
                   // inventory.addItem(PaletteGlobal.itemRegistry[world.getBlockAt(blockPickRequestPacket.position).name]!!)
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
                        println(actionPacket.action)
//                        val levelEventPacket = LevelEventPacket()
//                        val block = world.getBlockAt(actionPacket.coord)
//                        levelEventPacket.eventId = EVENT_BLOCK_START_BREAK
//                        levelEventPacket.position = actionPacket.coord
//                        levelEventPacket.data = (block.hardness * 5).toInt() //todo break time
//                        println(levelEventPacket)
//                        nettyQueue.add(levelEventPacket.gamePacket())
                    }
                    PlayerActionPacket.ACTION_JUMP -> {
                        foodLevel--
                    }
                }

            }
            MinecraftPacketConstants.ENTITY_FALL -> { println("ENTITY_FALL") }
            MinecraftPacketConstants.SET_ENTITY_DATA -> { println("SET_ENTITY_DATA") }
            MinecraftPacketConstants.SET_ENTITY_MOTION -> { println("SET_ENTITY_MOTION") }
            MinecraftPacketConstants.ANIMATE -> {

            }
            MinecraftPacketConstants.RESPAWN -> {
                RespawnPacket().let {
                    it.decode(inGamePacket.payload)
                    it.position = Float3(0f, 100f, 0f)
                    it.state = 1
                    nettyQueue.add(it.gamePacket())
                }
            }
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
                Registry.COMMAND_REGISTRY[commandArgs[0]].let {
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
            MinecraftPacketConstants.MALFORM_PACKET -> { MalformHandler(inGamePacket.payload) }
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
        logger.trace { "sent '$name' update chunk radius $renderDistance " }
        nettyQueue.add(ChunkRadiusUpdatePacket(renderDistance).gamePacket())
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