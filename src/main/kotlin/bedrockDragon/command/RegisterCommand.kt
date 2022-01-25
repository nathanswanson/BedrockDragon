package bedrockDragon.command

import bedrockDragon.registry.CommandRegistry

@CommandRegistryDSL
fun registerCommand(modName: String, registerList: RegisterCommand.() -> Unit) {
    RegisterCommand(modName).run(registerList)
}

@CommandRegistryDSL
class RegisterCommand(var modName: String) {
    @Suppress("UNCHECKED_CAST")
    fun command(name: String, lambda: Command.() -> Unit = {}) {
        val command = Command(name).apply(lambda)
        CommandRegistry.register(command.name, command)
    }
}