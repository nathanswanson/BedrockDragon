package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.VarInt
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.type.GameRules
import bedrockDragon.player.Player
import bedrockDragon.resource.RuntimeItemState
import com.curiouscreature.kotlin.math.Float2
import com.curiouscreature.kotlin.math.Float3
import java.util.*

class StartGamePacket: PacketPayload() {
    var entityIdSelf: Long = 1 //VarLong
    var runtimeEntityId: ULong = 2u //VarLong
    var playerGamemode: Int = 0 //sVarInt
    lateinit var spawn: Float3
    lateinit var rotation: Float2 //vector2
    var seed: Int = 0 //sVarInt
    var biomeType: Short = 0
    var customBiomeName: String = ""
    var dimension: Int = 0 //sVarInt
    var generator: Int = 1 //sVarInt
    var worldGamemode: Int = 0 //sVarInt
    var difficulty: Int = 0 //sVarInt
    lateinit var worldSpawn: Float3 //Todo blockCoord (sVarInt x, varInt y, sVarInt z)
    val hasAchievementsDisabled = false
    var dayCycleStopTime: Int = -1 //sVarInt
    val EDUOffer: Int = 0 //sVarInt
    val educationFeatures = false
    val educationProductId: String = ""
    var rainLevel: Float = 0f
    var lightningLevel: Float = 0f
    val hasConfirmedPlatLockContent = false
    val isMultiplayer = true
    val broadcastToLAN = true //todo
    var xboxLiveBroadcast: Int = 4
    var platformBroadcaseMode: Int = 4
    val enableCommands = true
    var texturePackRequired = false
    var gameRules = GameRules
    val bonusChest = false
    val mapEnabled = false
    var permissionLevel: Int = 1
    val serverTickRange: Int = 4 //arbitrary
    val lockedBehaviorPack = false
    val lockedResourcePack = false
    val MSANametagsOnly = false
    val isFromWorldTemplate = false
    val lockedWorldOptionTemplate = false
    val v1Villager = false
    var gameVersion: String = ""
    var limitedWorldWidth: Int = 16
    var limitedWorldHeight: Int = 16
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
    val multiplayerCorId = ""//: String = UUID.randomUUID().toString()
    var inventoryServerAuthoritative = false
    val serverEngine = ""

    override fun encode() {
        try {
            writeVarLong(1)//
            writeUnsignedVarLong(1)//
            writeVarInt(0)
           // writeVector3(spawn)
            writeFloatLE(0.0)
            writeFloatLE(0.0)
            writeFloatLE(0.0)

            //writeVector2(rotation)
            writeFloatLE(0.0)
            writeFloatLE(0.0)

            writeVarInt(-1)
            writeShortLE(0) //
            writeString("plains")
            writeVarInt(0)
            writeVarInt(1)
            writeVarInt(0)
            writeVarInt(1)

            //writeBlockCoordinates(worldSpawn) //
            writeVarInt(0)
            writeUnsignedVarInt(0)
            writeVarInt(0)

            writeBoolean(true)
            writeVarInt(-1)
            writeVarInt(0)
            writeBoolean(false)
            writeString("")
            writeFloatLE(0.0) //
            writeFloatLE(0.0) //
            writeBoolean(false)
            writeBoolean(true)
            writeBoolean(true)
            writeVarInt(4) //
            writeVarInt(4) //

            writeBoolean(true)
            writeBoolean(false)
            writeVarInt(0)// writeGameRules(gameRules)
            writeIntLE(0) //experiment count
            writeBoolean(false) //prev experiment
            writeBoolean(false)
            writeBoolean(false)
            writeVarInt(1)
            writeIntLE(4)//
            writeBoolean(false)
            writeBoolean(false)
            writeBoolean(false) //islockedworldtemplate
            writeBoolean(false)
            writeBoolean(false)
            writeBoolean(false)
            writeBoolean(false)
            writeString("1.17.41")

            writeIntLE(16)//v
            writeIntLE(16)//v
            writeBoolean(false)
            writeString("") // EduSharedUriResource buttonName
            writeString("") // EduSharedUriResource linkUri
            writeBoolean(false)//
            writeString("")
            writeString("drago")
            writeString("")
            writeBoolean(false)

            writeUnsignedVarInt(0)
            writeVarInt(0)
            writeBoolean(false)
            writeLongLE(0)
            writeVarInt(0)
            writeUnsignedVarInt(0) //blockmanifest
            writeUnsignedVarInt(0)//writeManifest()//v

            writeString("")
            writeBoolean(false)
            writeString("")

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
            //startGamePacket.entityIdSelf = player.entityIdSelf
            //startGamePacket.runtimeEntityId = player.runtimeEntityId
            startGamePacket.playerGamemode = player.gamemode.ordinal
            startGamePacket.spawn = Float3(0f,10f,0f)
            startGamePacket.rotation = Float2(0f,0f)
            startGamePacket.seed = 12345
            startGamePacket.biomeType = 0
            startGamePacket.customBiomeName = "plains"
            startGamePacket.dimension = 0 //overworld
            startGamePacket.generator = 1
            startGamePacket.worldGamemode = 0
            startGamePacket.difficulty = 1
            startGamePacket.worldSpawn = Float3(0f,0f,0f)
            startGamePacket.dayCycleStopTime = -1
            startGamePacket.rainLevel = 0f
            startGamePacket.lightningLevel = 0f
            startGamePacket.xboxLiveBroadcast = 4
            startGamePacket.platformBroadcaseMode = 4
            startGamePacket.texturePackRequired = false
            startGamePacket.gameRules = GameRules
            startGamePacket.permissionLevel = 1
            startGamePacket.gameVersion = ""
            startGamePacket.limitedWorldWidth = 16
            startGamePacket.limitedWorldHeight = 16
            startGamePacket.isNetherType = false
            startGamePacket.forceExperimental = false
            startGamePacket.levelId = "1m0AAMIFIgA="
            startGamePacket.worldName = "DRAGON"
            startGamePacket.movementType = 0
            startGamePacket.movementRewindSize = 0
            startGamePacket.serverAuthoritativeBlockBreaking = false
            startGamePacket.currentTick = 100000
            startGamePacket.enchantSeed = 12356
            startGamePacket.blockProperties = emptyArray()
            startGamePacket.inventoryServerAuthoritative = false
            return startGamePacket
        }
    }

    fun writeManifest() {
        val manifest = RuntimeItemState.parse()
        writeUnsignedVarInt(manifest.size)
        for(entry in manifest) {
            writeString(entry.name)
            writeShortLE(entry.id)
            writeBoolean(false) //component
        }
    }
}
