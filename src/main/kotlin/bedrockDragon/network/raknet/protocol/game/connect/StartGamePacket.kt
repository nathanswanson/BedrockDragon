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

package bedrockDragon.network.raknet.protocol.game.connect

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import bedrockDragon.network.raknet.protocol.game.type.gamerule.GameRules
import bedrockDragon.player.Player
import bedrockDragon.registry.Registry
import bedrockDragon.resource.RuntimeItemState
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import java.util.*

/**
 * Large payload that is sent shortly before player loading in. Contains important data
 * about what the client should expect.
 * @author Nathan Swanson
 * @since ALPHA
 */
class StartGamePacket: PacketPayload(MinecraftPacketConstants.START_GAME) {
    var entityIdSelf: Long = 1 //VarLong
    var runtimeEntityId: Long = 1 //VarLong
    var playerGamemode: Int = 0 //sVarInt
    lateinit var spawn: Float3
    lateinit var rotation: Float2 //vector2
    var seed: Long = -1L //sVarInt
    var biomeType: Short = 0
    var customBiomeName: String = "plains"
    var dimension: Int = 0 //sVarInt
    var generator: Int = 1 //sVarInt
    var worldGamemode: Int = playerGamemode //sVarInt
    var difficulty: Int = 1 //sVarInt
    lateinit var worldSpawn: Float3
    val hasAchievementsDisabled = true
    var dayCycleStopTime: Int = -1 //sVarInt
    val EDUOffer: Int = 0 //sVarInt
    val educationFeatures = false
    val educationProductId: String = ""
    var rainLevel: Float = 0f
    var lightningLevel: Float = 0f
    val hasConfirmedPlatLockContent = false
    val isMultiplayer = true
    val broadcastToLAN = true
    var xboxLiveBroadcast: Int = 4
    var platformBroadcaseMode: Int = 4
    val enableCommands = true
    var texturePackRequired = false
    var gameRules = GameRules
    val bonusChest = false
    val mapEnabled = false
    var permissionLevel: Int = 1
    val serverTickRange: Int = 4
    val lockedBehaviorPack = false
    val lockedResourcePack = false
    val MSANametagsOnly = false
    val isFromWorldTemplate = false
    val lockedWorldOptionTemplate = false
    val v1Villager = false
    var gameVersion: String = "*"
    var limitedWorldWidth: Int = 16
    var limitedWorldHeight: Int = 16
    var isNetherType = false
    var forceExperimental = false
    var levelId: String = ""
    var worldName: String = "A Bedrock Dragon Server"
    val premiumWorldId: String = ""
    val isTrial = false
    var movementType: Int = 0 //varInt
    var movementRewindSize: Int = 0
    var serverAuthoritativeBlockBreaking = false
    var currentTick: Long = 0 //Long LE
    var enchantSeed: Int = 0 //sVarInt
    val multiplayerCorId = ""//: String = UUID.randomUUID().toString()
    var inventoryServerAuthoritative = false
    val serverEngine = ""

    val prevExperimental: Boolean = false
    val experimentCount = 0
    val isLockedWorldTemplate: Boolean = false
    val blockRegistryChecksum = 0L


    //1.9.20
    var clientSideChunkGeneration: Boolean = true
    var chatRestrictionLevel: Byte = 0
    var playerInteractable: Boolean = true

    override suspend fun encode() {
        try {


            writeVarLong(entityIdSelf)
            writeUnsignedVarLong(runtimeEntityId)
            writeVarInt(playerGamemode)
            writeVector3(spawn)
            writeVector2(rotation)
            //world settings //
            writeLongLE(seed)
            writeShortLE(biomeType.toInt())
            writeString(customBiomeName)
            writeVarInt(dimension)
            writeVarInt(generator)
            writeVarInt(worldGamemode)
            writeVarInt(difficulty)
            writeBlockCoordinates(worldSpawn)
            writeBoolean(hasAchievementsDisabled)
            writeBoolean(false) //world editor
            writeVarInt(dayCycleStopTime)
            writeVarInt(EDUOffer)
            writeBoolean(educationFeatures)
            writeString(educationProductId)
            writeFloatLE(rainLevel.toDouble())
            writeFloatLE(lightningLevel.toDouble())
            writeBoolean(hasConfirmedPlatLockContent)
            writeBoolean(isMultiplayer)
            writeBoolean(broadcastToLAN)
            writeVarInt(xboxLiveBroadcast)
            writeVarInt(platformBroadcaseMode)
            writeBoolean(enableCommands)
            writeBoolean(texturePackRequired)
            writeGameRules(gameRules)
            writeIntLE(experimentCount)
            writeBoolean(prevExperimental)
            writeBoolean(bonusChest)
            writeBoolean(mapEnabled)
            writeVarInt(permissionLevel)
            writeIntLE(serverTickRange)
            writeBoolean(lockedBehaviorPack)
            writeBoolean(lockedResourcePack)
            writeBoolean(isLockedWorldTemplate)
            writeBoolean(MSANametagsOnly)
            writeBoolean(isFromWorldTemplate)
            writeBoolean(lockedWorldOptionTemplate)
            writeBoolean(v1Villager)
            writeBoolean(false)//disable persona
            writeBoolean(false)//disable custom skins
            writeString(gameVersion)
            writeIntLE(limitedWorldWidth)
            writeIntLE(limitedWorldHeight)
            writeBoolean(isNetherType)
            writeString("")
            writeString("")
            writeBoolean(forceExperimental)
            writeByte(0)//chat restriction
            writeBoolean(false)//player interaction
            //end world settings //
            writeString(levelId)
            writeString(worldName)
            writeString(premiumWorldId)
            writeBoolean(isTrial)
            writeVarInt(movementType)
            writeVarInt(movementRewindSize)
            writeBoolean(true) //block breaking
            writeLongLE(currentTick)
            writeVarInt(enchantSeed)
            writeUnsignedVarInt(0) //blockmanifest
            writeUnsignedVarInt(0) //item manifest
            writeString(multiplayerCorId)
            writeBoolean(inventoryServerAuthoritative)
            writeString(serverEngine) //server version
            writeUnsignedVarLong(0)
            writeLongLE(blockRegistryChecksum)
            //empty uuid
            writeLong(0)
            writeLong(0)
            writeBoolean(false)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            println(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }

    private fun writeManifest() {
        val manifest = RuntimeItemState.runtimeIdStates
        writeUnsignedVarInt(manifest.size)
        for(entry in manifest) {
            writeString(entry.name)
            writeShortLE(entry.id)
            writeBoolean(false) //component
        }
    }
}
