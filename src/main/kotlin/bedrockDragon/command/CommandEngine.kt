package bedrockDragon.command

import bedrockDragon.player.Player
import java.util.*

/**
 * [CommandEngine] attempts to turn a user command into a valid input.
 * @author Nathan Swanson
 * @since BETA
 */
object CommandEngine {
    fun invokeWith(args: Array<Any>, command: Command, player: Player) {
        var predictedFullArgument = LinkedList(args.asList())
        val totalNeededSize = command.args.size

        var readPtr = totalNeededSize - 1
        while (predictedFullArgument.size < totalNeededSize) {
            if(command.args[readPtr].optional) {
                //get the last optional that hasn't been used
                predictedFullArgument.add(readPtr,null)
            }
            readPtr--
        }

        command.invoke?.let { it(player, predictedFullArgument.toArray()) }
    }
}