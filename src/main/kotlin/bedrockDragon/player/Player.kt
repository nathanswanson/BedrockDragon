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

import bedrockDragon.chat.ChatRail
import bedrockDragon.command.CommandEngine
import bedrockDragon.entity.DataTag.DATA_BOUNDING_BOX_HEIGHT
import bedrockDragon.entity.DataTag.DATA_BOUNDING_BOX_WIDTH
import bedrockDragon.entity.DataTag.DATA_FLAGS
import bedrockDragon.entity.DataTag.DATA_FLAG_BREATHING
import bedrockDragon.entity.DataTag.DATA_FLAG_GRAVITY
import bedrockDragon.entity.DataTag.DATA_FLAG_HAS_COLLISION
import bedrockDragon.entity.DataTag.DATA_HEALTH
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
import bedrockDragon.network.raknet.protocol.game.connect.DisconnectPacket
import bedrockDragon.network.raknet.protocol.game.entity.EntityDataPacket
import bedrockDragon.network.raknet.protocol.game.entity.MobEquipmentPacket
import bedrockDragon.network.raknet.protocol.game.entity.MoveEntityDeltaPacket
import bedrockDragon.network.raknet.protocol.game.event.LevelEventPacket
import bedrockDragon.network.raknet.protocol.game.event.LevelEventPacket.Companion.EVENT_BLOCK_START_BREAK
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerClosePacket
import bedrockDragon.network.raknet.protocol.game.inventory.ContainerOpenPacket
import bedrockDragon.network.raknet.protocol.game.inventory.InventorySlotPacket
import bedrockDragon.network.raknet.protocol.game.inventory.InventoryTransactionPacket
import bedrockDragon.network.raknet.protocol.game.player.*
import bedrockDragon.network.raknet.protocol.game.ui.TextPacket
import bedrockDragon.network.raknet.protocol.game.world.*
import bedrockDragon.reactive.ISubscriber
import bedrockDragon.reactive.ReactivePacket
import bedrockDragon.registry.CommandRegistry
import bedrockDragon.resource.ServerProperties
import bedrockDragon.registry.WorldRegistry
import bedrockDragon.util.WorldInt2
import bedrockDragon.util.bgRed
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
import kotlin.math.log
import kotlin.text.StringBuilder

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

    //has player sent confirmation packet.
    var isConfirmed = false
    //Outgoing Packets
    val nettyQueue = ConcurrentLinkedQueue<MinecraftPacket>()

    private val entityIdSelf = runtimeEntityId

    var gamemode = Gamemode.CREATIVE
        set(value) {
            //send gamemodepacket
            val gamemodePacket = PlayerGameTypePacket()
            gamemodePacket.gamemode = value.ordinal
            nettyQueue.add(gamemodePacket.gamePacket())
            field = value
        }
    var isOp = false

    var world = WorldRegistry.getWorld(0)!!

    val inventory = PlayerInventory()
    val windowId = ConcurrentHashMap<Inventory, Int>()

    var skinData: Skin? = null
    private val playerMeta =  MetaTag()

    var renderDistance = 4
    var chunkRelay = world.getOrLoadRelay(position)
    /*
        NBT ENABLED VAR
     */

    //temp
    var sendChunkCoord = ArrayList<WorldInt2>()

    var foodLevel: Byte = 20
        set(value) {
            field = value

            //update client food level
            refreshAndSendAttribute()
        }
    var foodExhaustionLevel: Byte = 0
    var foodSaturationLevel: Byte = 0

    /**
     * [playInit] is called as soon as the Player is fully connected and joined the game.
     */
    fun playInit() {
        //NBT exists for player if so use those settings else use default and create one

        //register to Chat Rail
        ChatRail.DEFAULT.subscribe(this)
        chunkRelay.addPlayer(this)
       // scope.launch { tick() }
        val attribute = PlayerAttributePacket()
        attribute.runtimeEntityId = runtimeEntityId
        nettyQueue.add(attribute.gamePacket())

        sendAdventure()
        sendAttributes()
        sendMeta()

        loadDefaultInventories()
    }

    fun addItemToPlayerInventory(item: Item) {
        inventory.addItem(item)
        inventory.sendPacketContents(this)
    }

    private fun loadDefaultInventories() {
        addWindow(inventory, 0, isPermanent = true, isAlwaysOpen = true)


        inventory.addItem(Item.testItem())
        inventory.addItem(Item.testItem())

        val woodenSword = PaletteGlobal.itemRegistry["minecraft:wooden_sword"]!!
        woodenSword.iDurability = 20
        woodenSword.count = 1

        val diamondPickAxe = PaletteGlobal.itemRegistry["minecraft:diamond_pickaxe"]!!
        diamondPickAxe.iDurability = 20
        diamondPickAxe.count = 1

        inventory.addItem(diamondPickAxe)

        inventory.addItem(woodenSword)
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
     fun sendAttributes() {
        val attributePacket = PlayerAttributePacket()
        attributePacket.attributes[4].value = health
        attributePacket.attributes[5].value = 0.1f
        nettyQueue.add(attributePacket.gamePacket())
    }
    private fun sendCommands() {
        AvailableCommandsPacket().let {

        }
    }
    /**
     * [sendAdventure]
     */
    private fun sendAdventure() {
        val packet = AdventureSettingsPacket()

        for(type in AdventureSettings.Type.values()) {
            packet.flag(type.id, type.defaultValue)
        }

        packet.flags = 0
        packet.actionPermissions = 30
        packet.commandPermission = 1
        packet.permissionLevel = 2

        packet.userId = entityIdSelf

        nettyQueue.add(packet.gamePacket())
    }

    override fun getDrops(): List<Item> {
        return inventory.getContents().filterNotNull()
    }

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

    override suspend fun tick() {
        while (true) { //20 tps aprox.
            runWithDynamicDelay {
               // checkIfChunkUpdateRequired()
            }
            delay(1000)
        }


    }

    private fun runWithDynamicDelay(tasks: () -> Unit) {
        tasks.invoke()
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
        RespawnPacket().let {
            it.position = Float3(0f,0f,0f)
            it.runtimeEntityId = runtimeEntityId
            nettyQueue.add(it.gamePacket())
        }

        inventory.clear()
    }

    /**
     * Meta controls gravity, size, air, total air ...
     */
    private fun sendMeta() {
        var flag = 0L xor (1L shl DATA_FLAG_GRAVITY)
        flag = flag xor (1L shl DATA_FLAG_BREATHING)
        flag = flag xor (1L shl DATA_FLAG_HAS_COLLISION)

        playerMeta.put(DATA_FLAGS, MetaTag.TypedDefineTag.TAGLONG(flag))

       // playerMeta.put(DATA_BOUNDING_BOX_WIDTH, MetaTag.TypedDefineTag.TAGFLOAT(1f))
      //  playerMeta.put(DATA_BOUNDING_BOX_HEIGHT, MetaTag.TypedDefineTag.TAGFLOAT(2f))
        //playerMeta.put(DATA_HEALTH, MetaTag.TypedDefineTag.TAGINT(health.toInt()))

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

    private fun refreshAndSendAttribute() {

    }

    enum class Gamemode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }

    fun sendMessage(text: Any, type: Int = 0) {
        sendMessage(text.toString(), type)
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
        //this.position = position
        println(position)
//        MoveEntityDeltaPacket().let {
//            it.
//        }
    }

    //todo review
    fun updateAttributes() {

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
                sendMessage(actionPacket.action)
                //action type switch
                when(actionPacket.action) {
                    PlayerActionPacket.ACTION_START_BREAK -> {
                        val levelEventPacket = LevelEventPacket()
                        val block = world.getBlockAt(actionPacket.coord)
                        levelEventPacket.eventId = EVENT_BLOCK_START_BREAK
                        levelEventPacket.position = actionPacket.coord
                        levelEventPacket.data = (block.hardness * 5).toInt() //todo break time
                        println(levelEventPacket)
                        nettyQueue.add(levelEventPacket.gamePacket())
                    }
                    PlayerActionPacket.ACTION_JUMP -> {

                    }
                }

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
//                val adventurePacket = AdventureSettingsPacket()
//                adventurePacket.decode(inGamePacket.payload)
//                logger.info { adventurePacket.toString() }
//                nettyQueue.add(adventurePacket.gamePacket())
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
                CommandRegistry.getCommand(commandArgs[0])?.let {
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
        //todo this needs to apply chunk radius to player object
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
}