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
package bedrockDragon.network.raknet.identifier

import bedrockDragon.network.raknet.RakNet.parseIntPassive
import bedrockDragon.network.raknet.RakNet.parseLongPassive
import kotlin.jvm.JvmOverloads
import java.lang.NullPointerException
import java.lang.IllegalArgumentException
import kotlin.Throws
import java.lang.StringBuilder
import java.util.Objects

/**
 * Represents a Minecraft identifier.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v1.0.0
 */
class MinecraftIdentifier : Identifier {
    private var serverName: String? = null
    /**
     * Returns the server protocol.
     *
     * @return the server protocol.
     */
    /**
     * Sets the server protocol.
     *
     * @param serverProtocol
     * the new server protocol.
     */
    var serverProtocol = 0
    private var versionTag: String? = null
    /**
     * Returns the online player count.
     *
     * @return the online player count.
     */
    /**
     * Sets the online player count.
     *
     * @param onlinePlayerCount
     * the new online player count.
     */
    var onlinePlayerCount = 0
    /**
     * Returns the max player count.
     *
     * @return the max player count.
     */
    /**
     * Sets the max player count.
     *
     * @param maxPlayerCount
     * the new max player count.
     */
    var maxPlayerCount = 0

    /**
     * Returns the globally unique ID.
     *
     * @return the globally unique ID.
     */
    var globallyUniqueId: Long = 0
        private set
    private var worldName: String? = null
    private var gamemode: String? = null
    /**
     * Returns whether or not the identifier is using the legacy builder.
     *
     * @return `true` if the identifier is using the legacy builder,
     * `false` if the identifier is using the regular
     * builder.
     */
    /**
     * Enables/Disables the legacy builder.
     *
     * @param legacy
     * `true` to enable the legacy builder,
     * `false` to use the regular builder.
     */
    var isLegacyMode = false
    /**
     * Creates a Minecraft identifier.
     *
     * @param serverName
     * the server name.
     * @param serverProtocol
     * the server protocol.
     * @param versionTag
     * the version tag.
     * @param onlinePlayerCount
     * the online player count.
     * @param maxPlayerCount
     * the max player count.
     * @param guid
     * the globally unique ID.
     * @param worldName
     * the world name.
     * @param gamemode
     * the gamemode.
     * @throws IllegalArgumentException
     * if the `serverName`, `worldName`, or
     * `gamemode` contain the separator character
     * {@value #SEPARATOR}, or if the `versionTag` is
     * invalid.
     */
    /**
     * Creates a blank Minecraft identifier.
     */
   // @JvmOverloads
   // constructor(
   //     serverName: String? = null, serverProtocol: Int = 0, versionTag: String? = null, onlinePlayerCount: Int = 0,
    //    maxPlayerCount: Int = 0, guid: Long = 0, worldName: String? = null, gamemode: String? = null
  //  ) {
       // setServerName(serverName)
       // this.serverProtocol = serverProtocol
      //  setVersionTag(versionTag)
       // this.onlinePlayerCount = onlinePlayerCount
      ///  this.maxPlayerCount = maxPlayerCount
      //  setServerGloballyUniqueId(guid)
      //  setWorldName(worldName)
      //  setGamemode(gamemode)
      //  isLegacyMode = false
    //}

    /**
     * Creates a Minecraft identifier from an existing identifier.
     *
     * @param identifier
     * the identifier.
     * @throws NullPointerException
     * if the `identifier` or its contents are
     * `null`.
     * @throws IllegalArgumentException
     * if the `identifier` is not a Minecraft identifier
     * or there are missing fields.
     */
    constructor(identifier: Identifier?) : super(identifier!!) {
        if (identifier == null) {
            throw NullPointerException("Identifier cannot be null")
        } else if (identifier.build() == null) {
            throw NullPointerException("Identifier contents cannot be null")
        } else require(isMinecraftIdentifier(identifier)) { "Not a Minecraft identifier" }
        val data: Array<String?> = identifier.build().split(SEPARATOR).toTypedArray()
        require(data.size >= DATA_COUNT_LEGACY) { "Missing " + (DATA_COUNT_LEGACY - data.size) + " fields" }
        for (i in data.indices) {
            data[i] = if (data[i]!!.length > 0) data[i] else null
        }
        serverName = data[1]
        serverProtocol = parseIntPassive(data[2]!!)
        versionTag = data[3]
        onlinePlayerCount = parseIntPassive(data[4]!!)
        maxPlayerCount = parseIntPassive(data[5]!!)
        isLegacyMode = true
        if (data.size >= DATA_COUNT) {
            globallyUniqueId = parseLongPassive(data[6]!!)
            worldName = data[7]
            gamemode = data[8]
            isLegacyMode = false
        }
    }

    /**
     * Creates a Minecraft identifier from an existing identifier.
     *
     * @param identifier
     * the identifier.
     * @throws NullPointerException
     * if the `identifier` is `null`.
     * @throws IllegalArgumentException
     * if the `identifier` is not a Minecraft identifier
     * or there are missing fields.
     */
    constructor(identifier: String?) : this(Identifier(identifier!!)) {}

    /**
     * Returns the server name.
     *
     * @return the server name.
     */
    fun getServerName(): String? {
        return serverName
    }

    /**
     * Returns the version tag.
     *
     * @return the version tag.
     */
    fun getVersionTag(): String? {
        return versionTag
    }

    /**
     * Returns the world name.
     *
     * @return the world name.
     */
    fun getWorldName(): String? {
        return worldName
    }

    /**
     * Returns the gamemode.
     *
     * @return the gamemode.
     */
    fun getGamemode(): String? {
        return gamemode
    }

    /**
     * Sets the server name.
     *
     * @param serverName
     * the new server name.
     * @throws IllegalArgumentException
     * if the `serverName` contains the separator
     * character {@value #SEPARATOR}.
     */
    @Throws(IllegalArgumentException::class)
    fun setServerName(serverName: String?) {
        if (serverName != null) {
            require(!serverName.contains(SEPARATOR)) { "Server name cannot contain contain separator character" }
        }
        this.serverName = serverName
    }

    /**
     * Sets the version tag.
     *
     * @param versionTag
     * the new version tag.
     * @throws IllegalArgumentException
     * if the version tag is invalid.
     */
    @Throws(IllegalArgumentException::class)
    fun setVersionTag(versionTag: String?) {
        require(verifyVersionTag(versionTag)) { "Invalid version tag" }
        this.versionTag = versionTag
    }

    /**
     * Sets the globally unique ID.
     *
     * @param guid
     * the new globally unique ID.
     */
    fun setServerGloballyUniqueId(guid: Long) {
        globallyUniqueId = guid
    }

    /**
     * Sets the world name.
     *
     * @param worldName
     * the new world name.
     * @throws IllegalArgumentException
     * if the `worldName` contains the separator
     * character {@value #SEPARATOR}.
     */
    @Throws(IllegalArgumentException::class)
    fun setWorldName(worldName: String?) {
        if (worldName != null) {
            require(!worldName.contains(SEPARATOR)) { "World name cannot contain contain separator character" }
        }
        this.worldName = worldName
    }

    /**
     * Sets the gamemode.
     *
     * @param gamemode
     * the new gamemode.
     * @throws IllegalArgumentException
     * if the `gamemode` contains the separator character
     * {@value #SEPARATOR}.
     */
    @Throws(IllegalArgumentException::class)
    fun setGamemode(gamemode: String?) {
        if (gamemode != null) {
            require(!gamemode.contains(SEPARATOR)) { "Gamemode cannot contain contain separator character" }
        }
        this.gamemode = gamemode
    }

    /**
     * Converts the values to a Minecraft identifier string.
     *
     * @param values
     * the values to write to the identifier.
     * @return the built identifier text.
     * @throws NullPointerException
     * if `values` is `null`.
     */
    @Throws(NullPointerException::class)
    private fun createBuildString(vararg values: Any?): String {
        if (values == null) {
            throw NullPointerException("Values cannot be null")
        }
        val identifierBuilder = StringBuilder()
        identifierBuilder.append(HEADER + SEPARATOR)
        for (i in 0 until values.size) {
            identifierBuilder.append(if (values[i] != null) values[i] else "")
            identifierBuilder.append(if (i + 1 < values.size) SEPARATOR else "")
        }
        return identifierBuilder.toString()
    }

    override fun hashCode(): Int {
        return Objects.hash(
            serverName, serverProtocol, versionTag, onlinePlayerCount, maxPlayerCount, globallyUniqueId, worldName,
            gamemode, isLegacyMode
        )
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is MinecraftIdentifier) {
            return false
        }
        val mi = o
        return (serverName == mi.serverName && serverProtocol == mi.serverProtocol
                && versionTag == mi.versionTag && onlinePlayerCount == mi.onlinePlayerCount
                && maxPlayerCount == mi.maxPlayerCount && globallyUniqueId == mi.globallyUniqueId
                && worldName == mi.worldName && gamemode == mi.gamemode
                && isLegacyMode == mi.isLegacyMode)
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException
     * if the version tag is invalid.
     */
    @Throws(IllegalArgumentException::class)
    override fun build(): String {
        require(verifyVersionTag(versionTag)) { "Invalid version tag" }
        return if (isLegacyMode == true) {
            createBuildString(serverName, serverProtocol, versionTag, onlinePlayerCount, maxPlayerCount)
        } else createBuildString(
            serverName, serverProtocol, versionTag, onlinePlayerCount, maxPlayerCount, globallyUniqueId,
            worldName, gamemode
        )
    }

    companion object {
        /**
         * The header found at the beginning of a Minecraft identifier.
         *
         *
         * This allows for easy indication that the identifier is actually a
         * Minecraft identifier, rather than that of another game.
         */
        private const val HEADER = "MCPE"

        /**
         * The separator character used to easily split data from it into parseable
         * chunks.
         */
        private const val SEPARATOR = ";"

        /**
         * The amount of fields found in a Minecraft identifier when it is in legacy
         * mode.
         */
        private const val DATA_COUNT_LEGACY = 6

        /**
         * The amount of fields found in a Minecraft identifier.
         */
        private const val DATA_COUNT = 9

        /**
         * Returns whether or not the version tag is valid.
         *
         *
         * In order for a version tag to be valid, it can only have numbers or
         * periods. A `null` value is also valid, seeing as when the
         * identifier is being built no version will be placed inside the identifier
         * string.
         *
         * @param versionTag
         * the version tag.
         * @return `true` if the version tag is valid, `false`
         * otherwise.
         */
        private fun verifyVersionTag(versionTag: String?): Boolean {
            if (versionTag != null) {
                for (c in versionTag.toCharArray()) {
                    if ((c < '0' || c > '9') && c != '.') {
                        return false
                    }
                }
            }
            return true
        }

        /**
         * Returns whether or not the the identifier is a Minecraft identifier.
         *
         * @param identifier
         * the identifier to check.
         * @return `true` if the identifier is a Minecraft identifier,
         * `false` otherwise.
         */
        fun isMinecraftIdentifier(identifier: Identifier?): Boolean {
            return identifier?.build()?.startsWith(HEADER) ?: false
        }
    }
}