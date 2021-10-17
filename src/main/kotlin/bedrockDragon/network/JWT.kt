package bedrockDragon.network

import java.nio.charset.StandardCharsets
import java.util.*


class JWT(string: String) {
    val header: String
    val payload: String
    val signature: String

    init {
        val split = string.split(".")
        //TODO directicly encode bytes instead of the string.
        header = String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8)
        payload = String(Base64.getDecoder().decode(split[1]), StandardCharsets.UTF_8)
        //TODO AUTH SIGNATURE IMPORTANT
        signature = split[2]




    }
}

