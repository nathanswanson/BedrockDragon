package bedrockDragon.world

import bedrockDragon.player.Player
import bedrockDragon.reactive.type.ISubscriber
import com.curiouscreature.kotlin.math.Float2
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class ChunkRelay(x: Int, y: Int) {
    constructor(float2: Float2) : this(float2.x.toInt(), float2.y.toInt())

    val logger = KotlinLogging.logger {}


    init {
        logger.info { "ChunkRelay Initialized at x: $x y: $y" }
    }
    val chunks = Array<Chunk>(16) {Chunk()}

    var up: ChunkRelay? = null
    var down: ChunkRelay? = null
    var left: ChunkRelay? = null
    var right: ChunkRelay? = null

    //todo need data structure thats
    //thread safe
    //fast traverse
    //fast remove
    //fast add
    val subscribers = HashSet<ISubscriber>()
    fun addPlayer(player: Player) {
        subscribers.add(player)
    }

    fun passToNewRelay() {

    }
}