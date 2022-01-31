package bedrockDragon.command

import bedrockDragon.player.Player
import bedrockDragon.registry.DSLBase
import bedrockDragon.registry.Registry


@CommandDSL
sealed class Command(val name: String): DSLBase() {
    var args = mutableListOf<CommandTag<*>>()
    var invoke: ((Player, Array<Any?>) -> Unit)? = null //called by command manager *entry point*

    override fun toString(): String {
        return name
    }

    override fun clone(): Command {
        return CommandImpl(name).let {
            it.args = args
            it.invoke = invoke
            it
        }
    }

}

class CommandImpl(name: String): Command(name)

@CommandDSL
fun registerCommand(modName: String, registerList: RegisterCommand.() -> Unit) {
    RegisterCommand(modName).run(registerList)
}

@CommandDSL
class RegisterCommand(var modName: String) {
    fun command(name: String, lambda: Command.() -> Unit = {}) {
        val command = CommandImpl(name).apply(lambda)
        Registry.COMMAND_REGISTRY.register(command.name, command)
    }
}

