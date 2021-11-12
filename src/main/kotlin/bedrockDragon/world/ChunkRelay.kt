package bedrockDragon.world

import bedrockDragon.player.Player
import bedrockDragon.reactive.type.ISubscriber
import java.util.concurrent.ConcurrentHashMap

class ChunkRelay(x: Int, y: Int) {
    val chunks = Array<Chunk>(9) {Chunk()}

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