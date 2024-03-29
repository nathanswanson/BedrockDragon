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

package bedrockDragon.entity

import bedrockDragon.network.raknet.protocol.game.entity.RemoveEntityPacket
import bedrockDragon.player.Player
import bedrockDragon.registry.DSLBase
import bedrockDragon.registry.Registry
import bedrockDragon.util.ISavable
import bedrockDragon.util.aabb.AABB
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

/**
 * [Entity] is the base class for dropped items/blocks,mobs,players, ...
 * @author Nathan Swanson
 * @since ALPHA
 */

@EntityDSL
open class Entity(open var name: String = "entity"): ISavable, DSLBase() {
    override val fileName: Path
        get() = Path("Players/$uuid.nbt")

    var air: Short = 0
    var customName: String? = null
    var customNameVisible: Boolean? = null
    var fallDistance: Float = 0f
    var fire: Short = 0
    var glowing: Byte = 0
    var hasVisualFire: Boolean = true
    var id: String? = null
    var invulnerable: Boolean = false
    var motion = Float3(0f,0f,0f)
    var noGravity: Boolean = false
    var onGround: Boolean = true
    var inWater: Boolean = false
    var passengers = emptyArray<Entity>()
    var portalCooldown = 0
    open var position = Float3(38f,80f,22f)
    var rotation = Float3(0f,0f, 0f)
    var silent: Boolean? = null
    var tags: Array<NbtTag> = emptyArray()
    var ticksFrozen: Int? = null
    open var uuid: String = "Lia"

    open var boundingBox: AABB = AABB(1f,1f,1f)
    //World entity is on.
    var world = Registry.WORLD_REGISTRY[0] ?: throw NullPointerException("World does not exist for $position")
    //chunkRelay the entity is currently on.
    var chunkRelay = world.getOrLoadRelay(position)

    companion object { var nextId = 1L }
    val runtimeEntityId = nextId++

    var entityUniqueIdentifier = 0L

    open suspend fun showFor(players: List<Player>) {}

    suspend fun removeFor(players: List<Player>) {
        players.forEach { player ->
            player.nettyQueue.add(
                RemoveEntityPacket().let {
                    it.uniqueEntityId = runtimeEntityId
                    it.gamePacket()
                }
            )
        }
    }

    fun intersects(otherEntity: Entity): Boolean {
        return boundingBox.intersects(position, otherEntity.position, otherEntity.boundingBox)
    }

    open suspend fun handleIntersection(otherEntity: Entity) {

    }

    open fun destroy() {

    }

    //START BUILDER
    fun build(): Entity {
        return this //for future use
    }

    //var health = 0
    //var damage = 0

    open fun update() { /*50ms loop MAX*/

    }
    //END BUILDER

    override fun save(builder: NbtCompoundBuilder) {
        builder.put("Air", air)
        builder.put("FallDistance", fallDistance)
        builder.put("Fire", fire)
        builder.put("Glowing", glowing)
        builder.put("HasVisualFire", hasVisualFire)
        builder.put("Invulnerable", invulnerable)
        builder.putNbtList("Motion") {
            add(0.0)
            add(0.0)
            add(0.0)
        }
        builder.put("NoGravity", noGravity)
        builder.put("OnGround", onGround)
        //put("Passengers", passengers)
        builder.put("PortalCooldown", portalCooldown)
        builder.putNbtList("Pos") {
            add(position.x.toDouble())
            add(position.y.toDouble())
            add(position.z.toDouble())
        }
        builder.putNbtList("Rotation") {
            add(rotation.x.toDouble())
            add(rotation.y.toDouble())
        }
        silent?.let { builder.put("Silent", it) }
        //tags
        ticksFrozen?.let { builder.put("TicksFrozen", it) }
        builder.put("UUID", uuid)
    }

    override fun hashCode(): Int {
        return runtimeEntityId.hashCode() + "EntityId".hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

     override fun read() {
        if(fileName.exists()) {
            val nbt = Nbt {
                variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
                compression = NbtCompression.Gzip // None, Gzip, Zlib
                compressionLevel = null // in 0..9
                encodeDefaults = true
                ignoreUnknownKeys = true
                serializersModule = EmptySerializersModule
            }

            val tag: NbtTag = fileName.inputStream().use { input ->
                nbt.decodeFromStream(input)
            }
            val nbtList = (tag.nbtCompound[""] as NbtCompound)
            for(nbtTag in nbtList.iterator()) {
                when(nbtTag.key) {
                    "Motion" -> {
                        val dbl3 = nbtTag.value.nbtList.toList()

                        motion = Float3(
                            dbl3[0].nbtDouble.value.toFloat(),
                            dbl3[1].nbtDouble.value.toFloat(),
                            dbl3[2].nbtDouble.value.toFloat())
                    }
                    "Pos" -> {

                        val dbl3 = nbtTag.value.nbtList.toList()

                        position = Float3(
                            dbl3[0].nbtDouble.value.toFloat(),
                            dbl3[1].nbtDouble.value.toFloat(),
                            dbl3[2].nbtDouble.value.toFloat())


                    }
                    "Air" -> air = nbtTag.value.nbtShort.value
                    "NoGravity" -> noGravity = nbtTag.value.nbtByte.booleanValue
                    "FallDistance" -> fallDistance = nbtTag.value.nbtFloat.value
                    "Invulnerable" -> invulnerable = nbtTag.value.nbtByte.booleanValue
                    "Rotation" -> {
                        val dbl2 = nbtTag.value.nbtList.toList()
//                        rotation = Float2(
//                            dbl2[0].nbtDouble.value.toFloat(),
//                            dbl2[1].nbtDouble.value.toFloat()
//                        )
                    }
                    "Fire" -> fire = nbtTag.value.nbtShort.value
                    "OnGround" -> onGround = nbtTag.value.nbtByte.booleanValue
                    "Glowing" -> glowing = nbtTag.value.nbtByte.value
                    "HasVisualFire" -> hasVisualFire = nbtTag.value.nbtByte.booleanValue
                    "PortalCooldown" -> portalCooldown = nbtTag.value.nbtInt.value
                }
            }
        }
    }

}