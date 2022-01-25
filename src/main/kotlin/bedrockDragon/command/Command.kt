package bedrockDragon.command

import bedrockDragon.player.Player

@CommandDSL
fun command(name: String, command: Command.() -> Unit): Command {
    return Command(name).apply(command).build()
}

@CommandDSL
class Command(val name: String) {
    var args = mutableListOf<CommandTag<*>>()
    var invoke: ((Player, Array<Any?>) -> Unit)? = null //called by command manager *entry point*
    fun build() : Command {
        return this
    }

    override fun toString(): String {
        return name
    }
}

