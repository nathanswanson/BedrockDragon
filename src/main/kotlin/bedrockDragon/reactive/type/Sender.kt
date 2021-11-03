package bedrockDragon.reactive.type

/**
 * Sender
 *  ------> *
 * @constructor Create empty Sender
 */
interface Sender<T> {
    val subscribers: HashSet<Receiver<T>>

    fun send(sender: T)
    fun subscribe(receiver: Receiver<T>)
    fun unSubscribe(receiver: Receiver<T>)
}