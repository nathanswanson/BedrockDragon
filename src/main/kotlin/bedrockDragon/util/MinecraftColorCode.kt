package bedrockDragon.util


/**
 *
 */
object MinecraftColorCode {
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

    fun mineStringToAscii(string: String): String {
        val id = string[0]
        val content = string.substring(1)
        return when(id) {
            BLACK -> content.black()
            DARK_BLUE -> content.blue()
            else -> content
        }
    }
}