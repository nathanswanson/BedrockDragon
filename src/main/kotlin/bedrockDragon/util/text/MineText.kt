package bedrockDragon.util.text

class MineText(var content: String, var type: Type) {

    private var inGameText: String? = null
    private var consoleText: String? = null

    init {
        if(type == Type.GAME) inGameText else consoleText = content
    }

    fun asGameText() : String {
        if(inGameText == null)
            inGameText = consoleText!!.toMinecraft()
        return inGameText!!
    }

    fun asConsoleText(): String {
        if(consoleText == null)
            consoleText = inGameText!!.fromMinecraft()
        return consoleText!!
    }

    enum class Type {
        CONSOLE,
        GAME,
    }


}