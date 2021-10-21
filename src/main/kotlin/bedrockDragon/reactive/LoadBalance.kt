package bedrockDragon.reactive

/**
 * With how coroutines are set up they are all weak by themselves.
 * however, they can be detached from there process and set to a new one.
 *
 * 2 threads are seperated for low server operations,
 *  one is used for netty communication
 *  the other one is used for thread killer/coroutine killer
 *
 * The server then has coroutines floating between processes (only if needed)
 *  Chat is on its own coroutine (commands as well)
 *
 *  The rest of the coroutines are for serializing the world from the constant changes,
 *  and also handling all observers to then convert into netty packets.
 *
 *
 *
 * @constructor Create empty Load balance
 */
class LoadBalance {
}