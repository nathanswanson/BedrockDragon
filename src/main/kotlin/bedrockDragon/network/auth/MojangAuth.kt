/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * the above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.network.auth

import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.crypto.ECDSAVerifier
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

/**
 * Authenticates a player confirming they aer using a real client.
 * @author Nathan Swanson
 * @since ALPHA
 */
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