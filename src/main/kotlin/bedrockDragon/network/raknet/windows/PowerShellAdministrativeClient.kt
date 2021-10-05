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
package bedrockDragon.network.raknet.windows

import kotlin.Throws
import kotlin.jvm.JvmStatic
import java.lang.StringBuilder
import bedrockDragon.network.raknet.windows.PowerShellAdministrativeClient
import java.lang.Process
import java.util.HashMap
import java.lang.IllegalArgumentException
import bedrockDragon.network.raknet.windows.PowerShellCommand
import kotlin.jvm.Synchronized
import bedrockDragon.network.raknet.windows.PowerShellException
import java.lang.InterruptedException
import java.net.ServerSocket
import java.nio.charset.Charset
import java.net.URISyntaxException
import java.lang.NullPointerException
import java.lang.RuntimeException
import bedrockDragon.network.raknet.windows.UniversalWindowsProgram
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.util.Objects

/**
 * Used to execute commands with administrative privileges on Windows machines.
 *
 *
 * This is only ever needed if a command that requires administrative privileges
 * needs to be executed. Here, we must take special care not to use anything
 * that is not in the default Java library, as the way this class is called
 * prevents it from having access to the others libraries normally included with
 * JRakNet.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.10.0
 */
object PowerShellAdministrativeClient {
    private const val END_OF_TEXT = 0x03.toChar()
    private const val POWERSHELL_ADMINISTRATIVE_TIMEOUT = 10000
    private const val AUTHENTICATION_SUCCESS = 0x01

    /**
     * Converts the `InputStream` to a string. This will result in
     * the closing of the stream, as all available data will be read from it
     * during conversion.
     *
     * @param in
     * the stream to convert.
     * @return the converted stream.
     * @throws IOException
     * if an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun ioStr(`in`: InputStream): String {
        var str = String()
        var next: String? = null
        val reader = BufferedReader(InputStreamReader(`in`))
        while (reader.readLine().also { next = it } != null) {
            str += """
                $next
                
                """.trimIndent()
        }
        `in`.close()
        return if (str.length > 1) {
            str.substring(0, str.length - 1)
        } else str
    }

    /**
     * The main method for the administrative PowerShell process. This must act
     * as a main method since it is called through the JVM as a normal process.
     *
     * @param args
     * the arguments. The first should be the port the PowerShell
     * server is listening on, with the second being the password,
     * and the final being the command itself terminated by an
     * `ETX 0x03` character.
     * @throws IOException
     * if an I/O error occurs.
     */
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var client: Socket? = null
        try {
            // Parse arguments
            println("Parsing arguments...")
            var i = 0
            val port = args[i++].toInt()
            val password = args[i++].toLong()
            val commandBuilder = StringBuilder()
            commandLoop@ while ( /* Already declared */i < args.size) {
                for (j in 0 until args[i].length) {
                    if (args[i][j] == END_OF_TEXT) {
                        break@commandLoop
                    }
                    commandBuilder.append(args[i][j])
                }
                commandBuilder.append(if (i + 1 < args.size) " " else "")
                i++
            }
            println("Parsed arguments!")

            // Connect to server
            println("Connecting to server on port $port...")
            client = Socket("127.0.0.1", port)
            client.soTimeout = POWERSHELL_ADMINISTRATIVE_TIMEOUT
            val clientIn = DataInputStream(client.getInputStream())
            val clientOut = DataOutputStream(client.getOutputStream())
            println("Connected to server")

            // Authorize connection
            println("Authenticating with password $password...")
            clientOut.writeLong(password)
            clientOut.flush()
            val authenticationResult = clientIn.readInt()
            if (authenticationResult == AUTHENTICATION_SUCCESS) {
                println("Authenticated with server")
            } else {
                System.err.println("Failed to authenticate with server")
                clientIn.close()
                clientOut.close()
                client.close()
                return
            }

            // Execute command
            println("Executing administrative PowerShell command")
            val powerShell = Runtime.getRuntime().exec(commandBuilder.toString())
            powerShell.outputStream.close()
            powerShell.waitFor()
            powerShell.destroyForcibly()
            println("Executed administrative PowerShell command")

            // Send command output
            println("Sending PowerShell command output...")
            clientOut.writeUTF(ioStr(powerShell.errorStream).trim { it <= ' ' })
            clientOut.flush()
            clientOut.writeUTF(ioStr(powerShell.inputStream).trim { it <= ' ' })
            clientOut.flush()
            println("Sent PowerShell command output")

            // Shutdown client
            println("Shutting down client...")
            clientOut.close()
            client.close()
            println("Shutdown client")
        } catch (e: Exception) {
            e.printStackTrace()
            client?.close()
            System.exit(1)
        }
    }
}