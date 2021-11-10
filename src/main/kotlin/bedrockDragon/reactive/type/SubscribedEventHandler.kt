package bedrockDragon.reactive.type

import bedrockDragon.reactive.Reactor

interface SubscribedEventHandler {
    fun invoke(reactor: Reactor)
    fun isApplicable(reactor: Reactor)
}