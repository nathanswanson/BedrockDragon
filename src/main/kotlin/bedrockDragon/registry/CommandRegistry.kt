package bedrockDragon.registry

import bedrockDragon.command.Command

object CommandRegistry {
    private val commands = HashMap<String, Command>()

    fun register(id: String, command: Command): Boolean {
        return commands.putIfAbsent(id, command) == null
    }

    fun getCommand(id: String): Command? {
        return commands[id]
    }
}