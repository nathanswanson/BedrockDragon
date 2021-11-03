package bedrockDragon.reactive.type

//Subscribe and be able to publish with one object
/**
 * Transceiver
 *    <-------> *
 * @constructor Create empty Transceiver
 */
interface Transceiver<T>: Receiver<T>, Sender<T>
