package bedrockDragon.reactive.type


/**
 * Broadcaster
 *
 *          ^
 *         /
 * <----> * ------->
 *        \
 *         v
 * @constructor Create empty Broadcaster
 */
open class Broadcaster<T> {
    private val transceivers = HashSet<Transceiver<T>>()

    fun subscribe(transceiver: Transceiver<T>) {
        transceivers.add(transceiver)
    }

    fun unSubscribe(transceiver: Transceiver<T>) {
        transceivers.remove(transceiver)
    }

    fun receive(received: T) {
        for(subscriber in transceivers) {
            subscriber.receive(received)
        }
    }


}