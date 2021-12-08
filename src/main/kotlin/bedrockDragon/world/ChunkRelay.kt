package bedrockDragon.world

import bedrockDragon.network.world.WorldInt2
import bedrockDragon.player.Player
import bedrockDragon.reactive.type.ReactivePacket
import bedrockDragon.util.Region
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import mu.KotlinLogging


class ChunkRelay(val x: Int,val z: Int,val parent: Region?) {
    constructor(float2: WorldInt2, region: Region) : this(float2.x.toInt(), float2.y.toInt(), region)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val logger = KotlinLogging.logger {}
    //private val subscribers = HashSet<ISubscriber>() //TODO not thread safe very bad and wont work with more then one relay
    private val subscriptionSharedFlow = MutableSharedFlow<ReactivePacket<*>>()
    private val nonMutableFlow = subscriptionSharedFlow.asSharedFlow()
    //chunks are a 4x4 grid laid in an array and are absolute position in world
    // x = idx / 4  0: 0,1,2,3  1: 4,5,6,7 ...
    // y = idx mod 4     0: 0,4,8,12 1: 1,5,9,13...
    val chunks = Array<Chunk>(16) { i ->
        Chunk(
            WorldInt2(
                ((i shr 2) + (x shl 2)),
                ((i and 3) + (z shl 2))
            ),
            this
        )
    }



    var up: ChunkRelay? = null
    var down: ChunkRelay? = null
    var left: ChunkRelay? = null
    var right: ChunkRelay? = null


    fun addPlayer(player: Player) {
        scope.launch {
            nonMutableFlow.filter { player.filter(it) }
                .collectLatest {
                player.emitReactiveCommand(it)
            }
        }
    }

    fun invoke(reactivePacket: ReactivePacket<*>) {
        scope.launch {
            subscriptionSharedFlow.emit(reactivePacket)
        }
    }

    fun removePlayer(player: Player) {

    }

    fun passToNewRelay() {

    }

    fun decommission() {

    }

    override fun toString(): String {
        return "ChunkRelay: x:$x z:$z"
    }
}