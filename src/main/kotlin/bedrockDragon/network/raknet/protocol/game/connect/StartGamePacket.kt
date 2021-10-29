package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.player.Player
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.type.GameRules
import bedrockDragon.resource.RuntimeItemState
import com.curiouscreature.kotlin.math.Float2
import com.curiouscreature.kotlin.math.Float3
import java.util.*
import kotlin.random.Random

class StartGamePacket: PacketPayload() {
    var entityIdSelf: Long = 0 //VarLong
    var runtimeEntityId: ULong = 0u //VarLong
    var playerGamemode: Int = 0 //sVarInt
    lateinit var spawn: Float3
    lateinit var rotation: Float2 //vector2
    var seed: Int = 0 //sVarInt
    var biomeType: Short = 0
    var customBiomeName: String = ""
    var dimension: Int = 0 //sVarInt
    var generator: Int = 0 //sVarInt
    var worldGamemode: Int = 0 //sVarInt
    var difficulty: Int = 0 //sVarInt
    lateinit var worldSpawn: Float3 //Todo blockCoord (sVarInt x, varInt y, sVarInt z)
    val hasAchievementsDisabled = false
    var dayCycleStopTime: Int = 0 //sVarInt
    val EDUOffer: Int = 1 //sVarInt
    val educationFeatures = false
    val educationProductId: String = ""
    var rainLevel: Float = 0f
    var lightningLevel: Float = 0f
    val hasConfirmedPlatLockContent = false
    val isMultiplayer = true
    val broadcastToLAN = false //todo
    var xboxLiveBroadcast: Int = 0
    var platformBroadcaseMode: Int = 0
    val enableCommands = true
    var texturePackRequired = false
    var gameRules = GameRules
    val bonusChest = false
    val mapEnabled = false
    var permissionLevel: Int = 0
    val serverTickRange: Int = 20 //arbitrary
    val lockedBehaviorPack = false
    val lockedResourcePack = false
    val MSANametagsOnly = false
    val isFromWorldTemplate = false
    val lockedWorldTemplate = false
    val v1Villager = false
    var gameVersion: String = ""
    var limitedWorldWidth: Int = 0
    var limitedWorldHeight: Int = 0
    var isNetherType = false
    var forceExperimental = false
    var levelId: String = ""
    var worldName: String = "Dragon" //TODO make server name
    val premiumWorldId: String = ""
    val isTrial = false
    var movementType: Int = 0 //varInt
    var movementRewindSize: Int = 0
    var serverAuthoritativeBlockBreaking = false
    var currentTick: Long = 0 //Long LE
    var enchantSeed: Int = 0 //sVarInt
    lateinit var blockProperties: Array<String> //todo
    //itemStates //todo
    val multiplayerCorId: String = UUID.randomUUID().toString()
    var inventoryServerAuthoritative = false

    override fun encode() {
        try {
            VarInt.writeVarLong(entityIdSelf, outputStream)//
            VarInt.writeUnsignedVarLong(runtimeEntityId, outputStream)//
            VarInt.writeVarInt(playerGamemode, outputStream)
            writeVector3(spawn)
            writeVector2(rotation)//
            VarInt.writeVarInt(seed, outputStream) //
            writeShort(biomeType) //
            writeString(customBiomeName)
            VarInt.writeVarInt(dimension, outputStream)
            VarInt.writeVarInt(generator, outputStream)
            VarInt.writeVarInt(worldGamemode, outputStream)
            VarInt.writeVarInt(difficulty, outputStream)
            writeBlockCoordinates(worldSpawn) //
            writeBoolean(hasAchievementsDisabled)
            VarInt.writeVarInt(dayCycleStopTime, outputStream)
            VarInt.writeVarInt(EDUOffer, outputStream)
            writeBoolean(educationFeatures)
            writeString(educationProductId)
            writeFloat(rainLevel) //
            writeFloat(lightningLevel) //
            writeBoolean(hasConfirmedPlatLockContent)
            writeBoolean(isMultiplayer)
            writeBoolean(broadcastToLAN)
            VarInt.writeUnsignedVarInt(xboxLiveBroadcast, outputStream) //
            VarInt.writeUnsignedVarInt(platformBroadcaseMode, outputStream) //
            writeBoolean(enableCommands)
            writeBoolean(texturePackRequired)
            writeGameRules(gameRules)
            //
            //
            writeBoolean(bonusChest)
            writeBoolean(mapEnabled)
            VarInt.writeVarInt(permissionLevel, outputStream)
            writeInt(serverTickRange)//
            writeBoolean(lockedBehaviorPack)
            writeBoolean(lockedResourcePack)
            writeBoolean(MSANametagsOnly)
            writeBoolean(isFromWorldTemplate)
            writeBoolean(lockedWorldTemplate)
            writeBoolean(v1Villager)
            writeString(gameVersion)
            writeInt(limitedWorldWidth)//v
            writeInt(limitedWorldHeight)//v
            writeBoolean(isNetherType)
            //edu uri button
            //edu uri linkuri
            writeBoolean(forceExperimental)//
            writeString(levelId)
            writeString(worldName)
            writeString(premiumWorldId)
            writeBoolean(isTrial)
            VarInt.writeUnsignedVarInt(movementType, outputStream)
            writeInt(movementRewindSize)
            writeBoolean(serverAuthoritativeBlockBreaking)
            writeLongLE(currentTick)
            VarInt.writeVarInt(enchantSeed, outputStream)
            VarInt.writeUnsignedVarInt(0, outputStream)
            writeManifest()//v
            writeString(multiplayerCorId)
            writeBoolean(inventoryServerAuthoritative)
            //
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            println(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }

    companion object {
        fun capture(player: Player): StartGamePacket {
            val startGamePacket = StartGamePacket()
            startGamePacket.entityIdSelf = player.entityIdSelf
            startGamePacket.runtimeEntityId = player.runtimeEntityId
            startGamePacket.playerGamemode = player.gamemode.ordinal
            startGamePacket.spawn = Float3(0f,10f,0f)
            startGamePacket.rotation = Float2(0f,0f)
            startGamePacket.seed = 12389
            startGamePacket.biomeType = 0x00
            startGamePacket.customBiomeName = "Plains"
            startGamePacket.dimension = 0 //overworld
            startGamePacket.generator = 1
            startGamePacket.worldGamemode = 0
            startGamePacket.difficulty = 1
            startGamePacket.worldSpawn = Float3(0f,0f,0f)
            startGamePacket.dayCycleStopTime = -1
            startGamePacket.rainLevel = 0f
            startGamePacket.lightningLevel = 0f
            startGamePacket.xboxLiveBroadcast = 0
            startGamePacket.platformBroadcaseMode = 0
            startGamePacket.texturePackRequired = false
            startGamePacket.gameRules = GameRules
            startGamePacket.permissionLevel = 1
            startGamePacket.gameVersion = "1.17.40"
            startGamePacket.limitedWorldWidth = 0
            startGamePacket.limitedWorldHeight = 0
            startGamePacket.isNetherType = false
            startGamePacket.forceExperimental = false
            startGamePacket.levelId = "QzpcVXNlcnNcbWlubmVcRGVza3RvcFxpbnRlbGlqIHNjaG9vbFxCZWRyb2NrRHJhZ29u"
            startGamePacket.worldName = "DRAGON"
            startGamePacket.movementType = 0
            startGamePacket.movementRewindSize = 0
            startGamePacket.serverAuthoritativeBlockBreaking = false
            startGamePacket.currentTick = 100000
            startGamePacket.enchantSeed = 54123
            startGamePacket.blockProperties = emptyArray()
            //todo item states

            startGamePacket.inventoryServerAuthoritative = false
            return startGamePacket
        }
    }

    fun writeManifest() {
        val manifest = RuntimeItemState.parse()
        VarInt.writeUnsignedVarInt(manifest.size, outputStream)
        for(entry in manifest) {
            writeString(entry.name)
            writeShortLE(entry.id)
            writeBoolean(false) //component
        }
    }
}