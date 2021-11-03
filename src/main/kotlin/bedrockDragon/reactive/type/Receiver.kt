package bedrockDragon.reactive.type

/**
 * Receiver
 * <----- *
 * @constructor Create empty Receiver
 */
interface Receiver<T> {
    fun receive(received: T)
}