package bedrockDragon.network.auth

import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.ECDSAVerifier
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

object MojangAuth {
    const val MOJANG_PUBLIC_KEY_BASE64 =
        "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE" +
                "8ELkixyLcwlZryUQcu1TvPOm" +
                "I2B7vX83ndnWRUaXm74wFfa5" +
                "f/lwQNTfrLVHa2PmenpGI6Jh" +
                "IMUJaWZrjmMj90NoKNFSNBuK" +
                "dm8rYiXsfaz3K36x/1U26HpG" +
                "0ZxK/V1V"

    private val MOJANG_PUBLIC_KEY =
        generateKey(MOJANG_PUBLIC_KEY_BASE64)

    fun verifyXUIDFromChain(chains: List<JWSObject>): Boolean {
        var lastKey :ECPublicKey? = null
        var mojangVerified = false

        val iterator = chains.iterator()
        while (iterator.hasNext()) {
            val jwt = iterator.next()
            val x5u = jwt.header.x509CertURL
            val expectedKey = generateKey(x5u.toString())

            if(lastKey == null) {
                lastKey = expectedKey
            } else if (lastKey != expectedKey) {
                return false
            }

            if(!verify(lastKey, jwt)) {
                return false
            }

            if(mojangVerified) {
                return !iterator.hasNext()
            }

            if(lastKey == MOJANG_PUBLIC_KEY) {
                mojangVerified = true
            }

            val payload = jwt.payload.toJSONObject()
            val base64Key = payload.get("identityPublicKey")
            if(base64Key is String) {
                lastKey = generateKey(base64Key)
            }

        }
        return mojangVerified
    }

    private fun generateKey(base64: String): ECPublicKey {
        return KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(base64))) as ECPublicKey
    }

    private fun verify(key: ECPublicKey, jwt: JWSObject): Boolean {
        return try {
           jwt.verify(ECDSAVerifier(key))
        } catch (e: Exception) {
            false
        }
    }
}