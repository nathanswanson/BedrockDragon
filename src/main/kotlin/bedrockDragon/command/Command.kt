package bedrockDragon.command

import kotlin.reflect.KClass


//View Model DSL
fun Command(block: CommandBuilder.() -> Unit): Command { return CommandBuilder().apply(block).build() }

data class Command(val name: String,val args: List<CommandTag>)
data class CommandTag(val type: KClass<*>, val check: String, val optional: Boolean)



class COMMANDARGS: ArrayList<CommandTag>(){
    fun tag(block: CommandTagBuilder.() -> Unit) {
        add(CommandTagBuilder().apply(block).build())
    }
}

class CommandTagBuilder {
    var type: KClass<*>? = null
    var check = ""
    var optional = false

    fun build() : CommandTag = CommandTag(type!!, check, optional)
}

class CommandBuilder {
    var name = ""
    private val args = ArrayList<CommandTag>()

    fun args(block: COMMANDARGS.() -> Unit) {
        args.addAll(COMMANDARGS().apply(block))
    }

    fun build(): Command = Command(name, args)
}