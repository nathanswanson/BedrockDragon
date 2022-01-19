/*
 *    __     ______     ______     __  __     __   __     ______     ______  
 *   /\ \   /\  == \   /\  __ \   /\ \/ /    /\ "-.\ \   /\  ___\   /\__  _\
 *  _\_\ \  \ \  __<   \ \  __ \  \ \  _"-.  \ \ \-.  \  \ \  __\   \/_/\ \/  
 * /\_____\  \ \_\ \_\  \ \_\ \_\  \ \_\ \_\  \ \_\\"\_\  \ \_____\    \ \_\ 
 * \/_____/   \/_/ /_/   \/_/\/_/   \/_/\/_/   \/_/ \/_/   \/_____/     \/_/                                                                          
 *
 * the MIT License (MIT)
 *
 * Copyright (c) 2016-2020 "Whirvis" Trent Summerlin
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package bedrockDragon.network.raknet.protocol

import java.util.UUID
import java.util.HashMap
import kotlin.jvm.JvmOverloads
import java.util.Objects
import kotlin.Throws
import java.lang.IllegalArgumentException

/**
 * Used to signify which implementation of the RakNet protocol is being used by
 * a connection. This functionality has *no* guarantee of functioning
 * completely, as it is dependent on the implementation to implement this
 * feature themselves.
 *
 *
 * As of March 1st, 2019, the only known implementations using this connection
 * type protocol are:
 *
 *  * JRakNet by "Whirvis" Trent Summerlin
 *
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.9.0
 * @see bedrockDragon.network.raknet.identifier.Identifier Identifier
 */
class ConnectionType private constructor(
    uuid: UUID?, name: String, language: String?, version: String?, metadata: HashMap<String, String>?,
    vanilla: Boolean
) {
    /**
     * Returns the universally unique ID of the implementation.
     *
     * @return the universally unique ID of the implementation.
     */
    val uuid: UUID?

    /**
     * Returns the name of the implementation.
     *
     * @return the name of the implementation.
     */
    val name: String

    /**
     * Returns the programming language of the implementation.
     *
     * @return the programming language of the implementation.
     */
    val language: String?

    /**
     * Returns the version of the implementation.
     *
     * @return the version of the implementation.
     */
    val version: String?
    private val metadata: HashMap<String, String>

    /**
     * Returns whether or not the connection type is a [.VANILLA]
     * implementation.
     *
     * @return `true` if the connection type is a [.VANILLA]
     * implementation, `false` otherwise.
     */
    val isVanilla: Boolean
    /**
     * Creates a connection type implementation descriptor.
     *
     * @param uuid
     * the universally unique ID of the implementation
     * @param name
     * the name of the implementation.
     * @param language
     * the name of the programming language the implementation was
     * programmed in.
     * @param version
     * the version of the implementation.
     * @param metadata
     * the metadata of the implementation. Metadata for an
     * implementation can be created using the
     * [.createMetaData] method.
     */
    /**
     * Creates a connection type implementation descriptor.
     *
     * @param uuid
     * the universally unique ID of the implementation
     * @param name
     * the name of the implementation.
     * @param language
     * the programming language the implementation.
     * @param version
     * the version of the implementation.
     */
    @JvmOverloads
    constructor(
        uuid: UUID?,
        name: String,
        language: String?,
        version: String?,
        metadata: HashMap<String, String> = HashMap()
    ) : this(uuid, name, language, version, metadata, false) {
    }

    /**
     * Returns the value of the metadata with the specified key.
     *
     * @param key
     * the key of the value to retrieve.
     * @return the value associated with the key, `null` if there is
     * none.
     */
    fun getMetaData(key: String?): String? {
        return metadata[key]
    }

    /**
     * Returns a cloned copy of the metadata.
     *
     * @return a cloned copy of the metadata.
     */
    // Clone type is known
    val metaData: HashMap<String, String>
        get() = metadata.clone() as HashMap<String, String>

    /**
     * Returns whether or not this implementation and the specified
     * implementation are the same implementation based on the UUID.
     *
     *
     * If the UUID of both implementations are `null` then
     * `false` will be returned since we have no logical way of
     * telling if the two implementations are actually the same as there are no
     * UUIDs to compare.
     *
     * @param connectionType
     * the connection type.
     * @return `true` if both implementations are the same,
     * `false` otherwise.
     */
    fun `is`(connectionType: ConnectionType?): Boolean {
        if (connectionType == null) {
            return false // No implementation
        } else if (connectionType.uuid == null || uuid == null) {
            return false // No UUID
        }
        return uuid == connectionType.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid, name, language, version, metadata, isVanilla)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is ConnectionType) {
            return false
        }
        val ct = o
        return (uuid == ct.uuid && name == ct.name && language == ct.language
                && version == ct.version && metadata == ct.metadata
                && isVanilla == ct.isVanilla)
    }

    override fun toString(): String {
        return ("ConnectionType [uuid=" + uuid + ", name=" + name + ", language=" + language + ", version=" + version
                + ", metadata=" + metadata + ", vanilla=" + isVanilla + "]")
    }

    companion object {
        const val MAX_METADATA_VALUES = 0xFF
        val MAGIC = byteArrayOf(
            0x03.toByte(), 0x08.toByte(), 0x05.toByte(), 0x0B.toByte(), 0x43,
            0x54.toByte(), 0x49.toByte()
        )

        /**
         * Converts the metadata keys and values to a [HashMap].
         *
         * @param metadata
         * the metadata keys and values.
         * @return the metadata as a [HashMap].
         * @throws IllegalArgumentException
         * if there is a key without a value or if there are more than
         * {@value #MAX_METADATA_VALUES} metadata values.
         */
        @Throws(IllegalArgumentException::class)
        fun createMetaData(vararg metadata: String): HashMap<String, String> {
            require(metadata.size % 2 == 0) { "There must be a value for every key" }
            require(metadata.size / 2 <= MAX_METADATA_VALUES) { "Too many metadata values" }
            val metadataMap = HashMap<String, String>()
            var i = 0
            while (i < metadata.size) {
                metadataMap[metadata[i]] = metadata[i + 1]
                i += 2
            }
            return metadataMap
        }

        /**
         * A connection from a vanilla client or an unknown implementation.
         */
        val VANILLA = ConnectionType(null, "Vanilla", null, null, null, true)

        /**
         * A JRakNet connection.
         */
		@JvmField
		val JRAKNET = ConnectionType(
            UUID.fromString("504da9b2-a31c-4db6-bcc3-18e5fe2fb178"), "JRakNet", "Java", "2.12.4-SNAPSHOT"
        )
    }

    /**
     * Creates a connection type implementation descriptor.
     *
     * @param uuid
     * the universally unique ID of the implementation
     * @param name
     * the name of the implementation.
     * @param language
     * the name of the programming language the implementation was
     * programmed in.
     * @param version
     * the version of the implementation.
     * @param metadata
     * the metadata of the implementation. Metadata for an
     * implementation can be created using the
     * [.createMetaData] method.
     * @param vanilla
     * `true` if the implementation is a vanilla
     * implementation, `false` otherwise.
     * @throws IllegalArgumentException
     * if there are more than {@value #MAX_METADATA_VALUES} metadata
     * values.
     */
    init {
        this.uuid = uuid
        this.name = name
        this.language = language
        this.version = version
        this.metadata = metadata ?: HashMap()
        if (metadata != null) {
            require(metadata.size <= MAX_METADATA_VALUES) { "Too many metadata values" }
        }
        isVanilla = vanilla
    }
}