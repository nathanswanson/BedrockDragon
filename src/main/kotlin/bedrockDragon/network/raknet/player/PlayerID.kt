package bedrockDragon.network.raknet.player

import io.netty.util.concurrent.Future

class PlayerID(val name: String, authentication: String) {

    private val AUTH_SERVER = "https://authserver.mojang.com"

    private val AUTHENTICATE = "$AUTH_SERVER/authenticate"
    private val REFRESH = "$AUTH_SERVER/refresh"
    private val VALIDATE = "$AUTH_SERVER/validate"
    private val SIGN_OUT = "$AUTH_SERVER/signout"
    private val INVALIDATE = "$AUTH_SERVER/invalidate"

    private val authenticated = false

    suspend fun authenticate(): Boolean {
        return false
    }

    suspend fun refresh(): Boolean {
        return false
    }

    suspend fun validate(): Boolean {
        return false
    }

    suspend fun signOut(): Boolean {
        return false
    }

    suspend fun invalidate(): Boolean {
        return false
    }
}