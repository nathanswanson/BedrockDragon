package bedrockDragon.util

import bedrockDragon.reactive.ReactivePacket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicBoolean

class ContractByteArray(size: Int) {
    private var data = ByteArray(size)
    private val setWrite = AtomicBoolean(true)

    private val completed = AtomicBoolean(false)

    private val subscriptionSharedFlow = MutableSharedFlow<ByteArray>()
    private val nonMutableFlow = subscriptionSharedFlow.asSharedFlow()


    fun write(bytes: ByteArray) {
        data = bytes
        completed.set(true)
    }

    fun writable(): Boolean {
        return !setWrite.getAndSet(false)
    }

    fun read(): ByteArray? {
        if(completed.get()) {
            return data
        }
        return null
    }
}