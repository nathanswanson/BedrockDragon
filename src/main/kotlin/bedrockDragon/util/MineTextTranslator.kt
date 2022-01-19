package bedrockDragon.util

fun String.fromMinecraft() : String {
    val builder = StringBuilder()
    val r = Regex("(?<=§).+?((?=§)|\$(?![\\r\\n]))")
    val codes = r.findAll(this)
    for(code in codes) {
        //grab color code

        builder.append(code.value)
    }
    return builder.toString()
}

fun String.toMinecraft(): String {
    return this
}
