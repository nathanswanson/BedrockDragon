package bedrockDragon.entity

import bedrockDragon.util.ISavable
import bedrockDragon.util.nbt.NbtFile
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import kotlinx.serialization.modules.EmptySerializersModule
import net.benwoodworth.knbt.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.inputStream


abstract class Entity: ISavable {
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
    var passengers = emptyArray<Entity>()
    var portalCooldown = 0
    open var position = Float3(0f,0f,0f)
    var rotation = Float2(0f,0f)
    var silent: Boolean? = null
    var tags: Array<NbtTag> = emptyArray()
    var ticksFrozen: Int? = null
    open var uuid: String = "nathan"




    override fun save(builder: NbtCompoundBuilder) {
        builder.put("Air", air)
        builder.put("FallDistance", fallDistance)
        builder.put("Fire", fire)
        builder.put("Glowing", glowing)
        builder.put("HasVisualFire", hasVisualFire)
        builder.put("Invulnerable", invulnerable)
        builder.putNbtList<NbtDouble>("Motion") {
            add(0.0)
            add(0.0)
            add(0.0)
        }
        builder.put("NoGravity", noGravity)
        builder.put("OnGround", onGround)
        //put("Passengers", passengers)
        builder.put("PortalCooldown", portalCooldown)
        builder.putNbtList<NbtDouble>("Pos") {
            add(position.x.toDouble())
            add(position.y.toDouble())
            add(position.z.toDouble())
        }
        builder.putNbtList<NbtDouble>("Rotation") {
            add(rotation.x.toDouble())
            add(rotation.y.toDouble())
        }
        silent?.let { builder.put("Silent", it) }
        //tags
        ticksFrozen?.let { builder.put("TicksFrozen", it) }
        builder.put("UUID", uuid)
    }

     override fun read() {

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
                     rotation = Float2(
                         dbl2[0].nbtDouble.value.toFloat(),
                         dbl2[1].nbtDouble.value.toFloat()
                     )
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