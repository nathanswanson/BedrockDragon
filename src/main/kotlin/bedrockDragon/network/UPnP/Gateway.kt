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
package bedrockDragon.network.UPnP

import kotlin.Throws
import java.util.HashMap
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.traversal.DocumentTraversal
import java.util.StringTokenizer
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.traversal.NodeFilter
import java.lang.Exception
import java.net.*

/**
 *
 * @author Federico
 */
internal class Gateway(data: ByteArray?, private val iface: Inet4Address, private val routerip: InetAddress) {
    private var serviceType: String? = null
    private var controlURL: String? = null
    @Throws(Exception::class)
    private fun command(action: String, params: Map<String, String>?): Map<String, String?> {
        val ret: MutableMap<String, String?> = HashMap()
        var soap = """<?xml version="1.0"?>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Body><m:$action xmlns:m="$serviceType">"""
        if (params != null) {
            for ((key, value) in params) {
                soap += "<$key>$value</$key>"
            }
        }
        soap += "</m:$action></SOAP-ENV:Body></SOAP-ENV:Envelope>"
        val req = soap.toByteArray()
        val conn = URL(controlURL).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "text/xml")
        conn.setRequestProperty("SOAPAction", "\"$serviceType#$action\"")
        conn.setRequestProperty("Connection", "Close")
        conn.setRequestProperty("Content-Length", "" + req.size)
        conn.outputStream.write(req)
        val d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.inputStream)
        val iter = (d as DocumentTraversal).createNodeIterator(d.documentElement, NodeFilter.SHOW_ELEMENT, null, true)
        var n: Node
        while (iter.nextNode().also { n = it } != null) {
            try {
                if (n.firstChild.nodeType == Node.TEXT_NODE) {
                    ret[n.nodeName] = n.textContent
                }
            } catch (t: Throwable) {
            }
        }
        conn.disconnect()
        return ret
    }

    val gatewayIP: String
        get() = routerip.hostAddress
    val localIP: String
        get() = iface.hostAddress
    val externalIP: String?
        get() = try {
            val r = command("GetExternalIPAddress", null)
            r["NewExternalIPAddress"]
        } catch (t: Throwable) {
            null
        }

    fun openPort(port: Int, udp: Boolean): Boolean {
        require(!(port < 0 || port > 65535)) { "Invalid port" }
        val params: MutableMap<String, String> = HashMap()
        params["NewRemoteHost"] = ""
        params["NewProtocol"] = if (udp) "UDP" else "TCP"
        params["NewInternalClient"] = iface.hostAddress
        params["NewExternalPort"] = "" + port
        params["NewInternalPort"] = "" + port
        params["NewEnabled"] = "1"
        params["NewPortMappingDescription"] = "WaifUPnP"
        params["NewLeaseDuration"] = "0"
        return try {
            val r = command("AddPortMapping", params)
            r["errorCode"] == null
        } catch (ex: Exception) {
            false
        }
    }

    fun closePort(port: Int, udp: Boolean): Boolean {
        require(!(port < 0 || port > 65535)) { "Invalid port" }
        val params: MutableMap<String, String> = HashMap()
        params["NewRemoteHost"] = ""
        params["NewProtocol"] = if (udp) "UDP" else "TCP"
        params["NewExternalPort"] = "" + port
        return try {
            command("DeletePortMapping", params)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun isMapped(port: Int, udp: Boolean): Boolean {
        require(!(port < 0 || port > 65535)) { "Invalid port" }
        val params: MutableMap<String, String> = HashMap()
        params["NewRemoteHost"] = ""
        params["NewProtocol"] = if (udp) "UDP" else "TCP"
        params["NewExternalPort"] = "" + port
        return try {
            val r = command("GetSpecificPortMappingEntry", params)
            if (r["errorCode"] != null) {
                throw Exception()
            }
            r["NewInternalPort"] != null
        } catch (ex: Exception) {
            false
        }
    }

    init {
        var location: String? = null
        val st = StringTokenizer(String(data!!), "\n")
        while (st.hasMoreTokens()) {
            val s = st.nextToken().trim { it <= ' ' }
            if (s.isEmpty() || s.startsWith("HTTP/1.") || s.startsWith("NOTIFY *")) {
                continue
            }
            val name = s.substring(0, s.indexOf(':'))
            val `val` = if (s.length >= name.length) s.substring(name.length + 1).trim { it <= ' ' } else null
            if (name.equals("location", ignoreCase = true)) {
                location = `val`
            }
        }
        if (location == null) {
            throw Exception("Unsupported Gateway")
        }
        val d: Document
        d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(location)
        val services = d.getElementsByTagName("service")
        for (i in 0 until services.length) {
            val service = services.item(i)
            val n = service.childNodes
            var serviceType: String? = null
            var controlURL: String? = null
            for (j in 0 until n.length) {
                val x = n.item(j)
                if (x.nodeName.trim { it <= ' ' }.equals("serviceType", ignoreCase = true)) {
                    serviceType = x.firstChild.nodeValue
                } else if (x.nodeName.trim { it <= ' ' }.equals("controlURL", ignoreCase = true)) {
                    controlURL = x.firstChild.nodeValue
                }
            }
            if (serviceType == null || controlURL == null) {
                continue
            }
            if (serviceType.trim { it <= ' ' }.toLowerCase()
                    .contains(":wanipconnection:") || serviceType.trim { it <= ' ' }
                    .toLowerCase().contains(":wanpppconnection:")
            ) {
                this.serviceType = serviceType.trim { it <= ' ' }
                this.controlURL = controlURL.trim { it <= ' ' }
            }
        }
        if (controlURL == null) {
            throw Exception("Unsupported Gateway")
        }
        val slash = location.indexOf("/", 7) //finds first slash after http://
        if (slash == -1) {
            throw Exception("Unsupported Gateway")
        }
        location = location.substring(0, slash)
        if (!controlURL!!.startsWith("/")) {
            controlURL = "/$controlURL"
        }
        controlURL = location + controlURL
    }
}