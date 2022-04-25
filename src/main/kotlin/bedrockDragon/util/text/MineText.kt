package bedrockDragon.util.text

class MineText(var content: String, var color: ColorCode) {
    var prev: MineText? = null
    var next: MineText? = null

    operator fun plus(other: MineText): MineText {
        next = other
        other.prev = this

        return other
    }

    operator fun plus(other: String): MineText {
        val otherMine = other.WHITE()
        next = otherMine
        otherMine.prev = this

        return otherMine
    }

    fun console(): String {
        var string = ""
        var node: MineText? = this

        do {
            string = "\u001b" + node!!.color.asciiCode + node.content +  "\u001b" + reset + string
            node = node?.prev
        } while(node != null)

        return string
    }

    fun minecraft(): String {
        var string = ""
        var node: MineText? = this

        do {
            string = "§" + node!!.color.mineCode + node.content + string
            node = node?.prev
        } while(node != null)

        return string
    }

    override fun toString(): String {
        return content
    }
}

data class ColorCode(val mineCode: Char, val asciiCode: String)

fun String.BLACK() = MineText(this, ColorCode(BLACK, black))
fun String.DARK_BLUE() = MineText(this, ColorCode(DARK_BLUE, blue))
fun String.DARK_GREEN() = MineText(this, ColorCode(DARK_GREEN, green))
fun String.DARK_AQUA() = MineText(this, ColorCode(DARK_AQUA, cyan))
fun String.DARK_RED() = MineText(this, ColorCode(DARK_RED, red))
fun String.DARK_PURPLE() = MineText(this, ColorCode(DARK_PURPLE, magenta))
fun String.GOLD() = MineText(this, ColorCode(GOLD, yellow))
fun String.GRAY() = MineText(this, ColorCode(GRAY, white))
fun String.BLUE() = MineText(this, ColorCode(BLUE, brightBlue))
fun String.GREEN() = MineText(this, ColorCode(GREEN, brightGreen))
fun String.AQUA() = MineText(this, ColorCode(AQUA, brightCyan))
fun String.RED() = MineText(this, ColorCode(RED, brightRed))
fun String.LIGHT_PURPLE() = MineText(this, ColorCode(LIGHT_PURPLE, brightMagenta))
fun String.YELLOW() = MineText(this, ColorCode(YELLOW, brightYellow))
fun String.WHITE() = MineText(this, ColorCode(WHITE, reset))
fun String.MINECOIN_GOLD() = MineText(this, ColorCode(MINECOIN_GOLD, brightYellow))
fun String.OBFUSCATED() = MineText(this, ColorCode(OBFUSCATED, bgBlack + "\u001b" + black))
fun String.BOLD() = MineText(this, ColorCode(BOLD, bold))
fun String.ITALIC() = MineText(this, ColorCode(ITALIC, italic))
fun String.RESET() = MineText(this, ColorCode(RESET, reset))

const val reset = "[0m"
const val bold = "[1m"
const val italic = "[3m"
const val underline = "[4m"
const val reversed = "[7m"
const val black = "[30m"
const val blue = "[34m"
const val cyan = "[36m"
const val green = "[32m"
const val magenta = "[35m"
const val red = "[31m"
const val white = "[37m"
const val yellow = "[33m"
const val brightBlack = "[30;1m"
const val brightBlue = "[34;1m"
const val brightCyan = "[36;1m"
const val brightGreen = "[32;1m"
const val brightMagenta = "[35;1m"
const val brightRed = "[31;1m"
const val brightWhite = "[37;1m"
const val brightYellow = "[33;1m"
const val bgBlack = "[40m"



//MINECRAFT
const val BLACK = '0'
const val DARK_BLUE = '1'
const val DARK_GREEN = '2'
const val DARK_AQUA = '3'
const val DARK_RED = '4'
const val DARK_PURPLE = '5'
const val GOLD = '6'
const val GRAY = '7'
const val DARK_GRAY = '8'
const val BLUE = '9'
const val GREEN = 'a'
const val AQUA = 'b'
const val RED = 'c'
const val LIGHT_PURPLE = 'd'
const val YELLOW = 'e'
const val WHITE = 'f'
const val MINECOIN_GOLD = 'g'
const val OBFUSCATED = 'k'
const val BOLD = 'i'
//Strike JAVA
//Underline JAVA
const val ITALIC = 'o'
const val RESET = 'r'