/*
 *
 *  *       *    *  /#   /  /  * /    /  /   *     /  /   /                     *     *     *     *     *     *    #   *       /       *   / *  /   * /      * #                                                                                #                                              *   *                                                                                                                        / *                                                                                                                       /      * the MIT License (MIT)
 *  *
 *  * Copyright (c) 2021-2021 Nathan Swanson
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * the above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package bedrockDragon.network.minecraft.json

import com.philjay.jwt.JWTAuthHeader
import com.philjay.jwt.JWTAuthPayload
import com.philjay.jwt.JsonDecoder
import kotlinx.serialization.json.buildJsonObject

object JWTPacketUtil {
    fun serialize() {

    }

    fun deserialize(message: String) {
        val decoder = object : JsonDecoder<JWTAuthHeader,JWTAuthPayload> {
            override fun headerFrom(json: String): JWTAuthHeader {
                buildJsonObject { }
            }

            override fun payloadFrom(json: String): JWTAuthPayload {
                TODO("Not yet implemented")
            }

        }
    }


}