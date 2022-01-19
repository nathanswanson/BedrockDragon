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

import java.util.LinkedList
import java.net.*

/**
 *
 * @author Federico
 */
internal abstract class GatewayFinder {
    companion object {
        private val SEARCH_MESSAGES: Array<String>
        private val localIPs: Array<Inet4Address>
            private get() {
                val ret = LinkedList<Inet4Address>()
                try {
                    val ifaces = NetworkInterface.getNetworkInterfaces()
                    while (ifaces.hasMoreElements()) {
                        try {
                            val iface = ifaces.nextElement()
                            if (!iface.isUp || iface.isLoopback || iface.isVirtual || iface.isPointToPoint) {
                                continue
                            }
                            val addrs = iface.inetAddresses ?: continue
                            while (addrs.hasMoreElements()) {
                                val addr = addrs.nextElement()
                                if (addr is Inet4Address) {
                                    ret.add(addr)
                                }
                            }
                        } catch (t: Throwable) {
                        }
                    }
                } catch (t: Throwable) {
                }
                return ret.toArray(arrayOf())
            }

        init {
            val m = LinkedList<String>()
            for (type in arrayOf(
                "urn:schemas-upnp-org:device:InternetGatewayDevice:1",
                "urn:schemas-upnp-org:service:WANIPConnection:1",
                "urn:schemas-upnp-org:service:WANPPPConnection:1"
            )) {
                m.add(
                    """
    M-SEARCH * HTTP/1.1
    HOST: 239.255.255.250:1900
    ST: $type
    MAN: "ssdp:discover"
    MX: 2
    
    
    """.trimIndent()
                )
            }
            SEARCH_MESSAGES = m.toArray<String>(arrayOf<String>())
        }
    }

    private inner class GatewayListener(ip: Inet4Address, req: String) : Thread() {
        private val ip: Inet4Address
        private val req: String
        override fun run() {
            var foundgw = false
            var gw: Gateway? = null
            try {
                val req = req.toByteArray()
                val s = DatagramSocket(InetSocketAddress(ip, 0))
                s.send(DatagramPacket(req, req.size, InetSocketAddress("239.255.255.250", 1900)))
                s.soTimeout = 3000
                while (true) {
                    try {
                        val recv = DatagramPacket(ByteArray(1536), 1536)
                        s.receive(recv)
                        gw = Gateway(recv.data, ip, recv.address)
                        val extIp = gw.externalIP
                        if (extIp != null && !extIp.equals(
                                "0.0.0.0",
                                ignoreCase = true
                            )
                        ) { //Exclude gateways without an external IP
                            gatewayFound(gw)
                            foundgw = true
                        }
                    } catch (t: SocketTimeoutException) {
                        break
                    } catch (t: Throwable) {
                    }
                }
            } catch (t: Throwable) {
            }
            if (!foundgw && gw != null) { //Pick the last GW if none have an external IP - internet not up yet??
                gatewayFound(gw)
            }
        }

        init {
            name = "WaifUPnP - Gateway Listener"
            this.ip = ip
            this.req = req
        }
    }

    private val listeners = LinkedList<GatewayListener>()
    val isSearching: Boolean
        get() {
            for (l in listeners) {
                if (l.isAlive) {
                    return true
                }
            }
            return false
        }

    abstract fun gatewayFound(g: Gateway?)

    init {
        for (ip in localIPs) {
            for (req in SEARCH_MESSAGES) {
                val l = GatewayListener(ip, req)
                l.start()
                listeners.add(l)
            }
        }
    }
}