/*
 * Copyright (C) 2015 Federico Dossena (adolfintel.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package bedrockDragon.network.upnp

import java.lang.InterruptedException

/**
 * This class contains static methods that allow quick access to UPnP Port Mapping.<br></br>
 * Commands will be sent to the default gateway.
 *
 * @author Federico
 */
object UPnP {
    private var defaultGW: Gateway? = null
    private val finder: GatewayFinder = object : GatewayFinder() {
        override fun gatewayFound(g: Gateway?) {
            synchronized(this) {
                if (defaultGW == null) {
                    defaultGW = g
                }
            }
        }
    }

    /**
     * Waits for UPnP to be initialized (takes ~3 seconds).<br></br>
     * It is not necessary to call this method manually before using UPnP functions
     */
    fun waitInit() {
        while (finder.isSearching) {
            try {
                Thread.sleep(1)
            } catch (ex: InterruptedException) {
            }
        }
    }

    /**
     * Is there an UPnP gateway?<br></br>
     * This method is blocking if UPnP is still initializing<br></br>
     * All UPnP commands will fail if UPnP is not available
     *
     * @return true if available, false if not
     */
    val isUPnPAvailable: Boolean
        get() {
            waitInit()
            return defaultGW != null
        }

    /**
     * Opens a TCP port on the gateway
     *
     * @param port TCP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    fun openPortTCP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.openPort(port, false)
    }

    /**
     * Opens a UDP port on the gateway
     *
     * @param port UDP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    fun openPortUDP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.openPort(port, true)
    }

    /**
     * Closes a TCP port on the gateway<br></br>
     * Most gateways seem to refuse to do this
     *
     * @param port TCP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    fun closePortTCP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.closePort(port, false)
    }

    /**
     * Closes a UDP port on the gateway<br></br>
     * Most gateways seem to refuse to do this
     *
     * @param port UDP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    fun closePortUDP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.closePort(port, true)
    }

    /**
     * Checks if a TCP port is mapped<br></br>
     *
     * @param port TCP port (0-65535)
     * @return true if the port is mapped, false otherwise
     */
    fun isMappedTCP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.isMapped(port, false)
    }

    /**
     * Checks if a UDP port is mapped<br></br>
     *
     * @param port UDP port (0-65535)
     * @return true if the port is mapped, false otherwise
     */
    fun isMappedUDP(port: Int): Boolean {
        return if (!isUPnPAvailable) false else defaultGW!!.isMapped(port, true)
    }

    /**
     * Gets the external IP address of the default gateway
     *
     * @return external IP address as string, or null if not available
     */
    val externalIP: String?
        get() = if (!isUPnPAvailable) null else defaultGW?.gatewayIP

    /**
     * Gets the internal IP address of this machine
     *
     * @return internal IP address as string, or null if not available
     */
    val localIP: String?
        get() = if (!isUPnPAvailable) null else defaultGW?.gatewayIP

    /**
     * Gets the  IP address of the router
     *
     * @return internal IP address as string, or null if not available
     */
    val defaultGatewayIP: String?
        get() = if (!isUPnPAvailable) null else defaultGW?.gatewayIP
}