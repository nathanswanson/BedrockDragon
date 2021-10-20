package bedrockDragon.reactive

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import java.util.concurrent.ConcurrentLinkedQueue


@Deprecated("Use Reactive streams")
class Event<T> : Publisher<T> {


    //DRAGON EVENT PROTOCOL
    interface Signal  {}
    enum class Cancel : Signal {Instance}
    enum class Subscribe : Signal {Instance}
    enum class Send : Signal {Instance}
    inner class Request(val n: Long) : Signal

    override fun subscribe(s: Subscriber<in T>?) {
        Subscription(s)
    }

    // This is our implementation of the Reactive Streams `Subscription`,
    // which represents the association between a `Publisher` and a `Subscriber`.
    inner class Subscription(val s: Subscriber<in T>?) : org.reactivestreams.Subscription {

        private var cancelled = false

        //`Subscription.request` sends a signal to the Subscription that more elements are in demand
        override fun request(n: Long) {
            signal(Request(n))
        }

        //`Subscription.cancel` sends a signal to the Subscription that the `Subscriber` is not interested in any more elements
        override fun cancel() {
            signal(Cancel.Instance)
        }

        // What `signal` does is that it sends signals to the `Subscription` asynchronously runs of a shared event coroutine
        fun signal(signal: Signal) {
            eventProcessQue.add(signal)
        }

        init {
            signal(Subscribe.Instance)
        }
    }

    //Event Processor shared between every type of event
    companion object {
        private val scope = CoroutineScope(Job() + Dispatchers.IO)

        //Pretty sure this fails 1.3 of reactive streams so TODO()
        private var eventProcessQue = ConcurrentLinkedQueue<Signal>()

        init {
            scope.launch { loop() }
        }

        private fun loop() {

            val signal = eventProcessQue.poll()
            //if canceled TODO()
            if(signal is Event<*>.Request) {

            }
            else if(signal == Send.Instance) {

            }
            else if(signal == Cancel.Instance) {

            }
            else if(signal == Subscribe.Instance) {

            }
        }
    }
}