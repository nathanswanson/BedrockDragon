package bedrockDragon.reactive.type

interface ISubscriber {
    fun filter(reactivePacket: ReactivePacket<*>): Boolean
}