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

import bedrockDragon.network.raknet.windows.PowerShellCommand
import bedrockDragon.network.raknet.windows.PowerShellException
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.*

/**
 * A command that can be executed in the Windows PowerShell environment under
 * the Windows 10 operating system.
 *
 *
 * Commands can be created and executed on devices that do not have Windows
 * PowerShell. However, they will ultimately not be executed.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.10.0
 */
class PowerShellCommand(
    /**
     * Returns the command string.
     *
     * @return the command string.
     */
    //private final Logger logger;
    val command: String
) {
    private val arguments: HashMap<String, String>

    /**
     * Sets an argument.
     *
     * @param argumentName
     * the argument name.
     * @param value
     * the value.
     * @return the command.
     * @throws IllegalArgumentException
     * if the `argumentName` does not begin with
     * {@value #ARGUMENT_PREFIX}.
     */
    @Throws(IllegalArgumentException::class)
    fun setArgument(argumentName: String, value: Any?): PowerShellCommand {
        require(argumentName.startsWith(ARGUMENT_PREFIX)) { "Argument name must begin with the argument prefix" }
        val valueStr = StringBuilder()
        valueStr.append(value ?: "null")
        require(!valueStr.toString().contains(ARGUMENT_PREFIX)) { "Value may not contain argument prefix" }
        arguments[argumentName] = valueStr.toString()
        //logger.debug("Set \"" + argumentName + "\" value to " + valueStr.toString());
        return this
    }

    /**
     * Returns the value of the argument.
     *
     * @param argumentName
     * the argument name.
     * @return the value of the argument, `null` if the argument has
     * not been set.
     * @throws IllegalArgumentException
     * if the `argumentName` does not begin with the
     * {@value #ARGUMENT_PREFIX} character.
     */
    @Throws(IllegalArgumentException::class)
    fun getArgument(argumentName: String): String? {
        require(argumentName.startsWith(ARGUMENT_PREFIX)) { "Argument name must begin with the argument prefix" }
        return arguments[argumentName]
    }

    /**
     * Executes the command. Once the command has been executed, its arguments
     * will be cleared so they do not linger in the case the same command with
     * different arguments is executed.
     *
     *
     * Take note that a `PowerShellException` not being thrown is not
     * an indication that the command actually executed. Rather, it just means
     * that the execution of this method in particular did not fail. The main
     * case of this is being a command not being executed either because the
     * machine is not running on Windows 10 or that it requires elevation but
     * the user declined.
     *
     * @param requiresElevation
     * `true` if the PowerShell command should be executed
     * under an elevated process, `false` otherwise.
     * @return the execution result.
     * @throws PowerShellException
     * if a PowerShell error occurs.
     */
    @Synchronized
    @Throws(PowerShellException::class)
    fun execute(requiresElevation: Boolean): String {
        // Create encoded command with arguments
        var command = POWERSHELL_EXECUTABLE + " -EncodedCommand "
        var encodedCommand = this.command
        for (argumentKey in arguments.keys) {
            encodedCommand = encodedCommand.replace(argumentKey, arguments[argumentKey]!!)
        }
        arguments.clear()
        command += Base64.getEncoder().encodeToString(encodedCommand.toByteArray(POWERSHELL_BASE64_CHARSET))
        return if (requiresElevation == false) {
            // Create process and execute command
            var powerShell: Process? = null
            try {
                powerShell = Runtime.getRuntime().exec(command)
                powerShell.outputStream.close()
                powerShell.waitFor()
                if (powerShell.exitValue() != 0) {
                    return RESULT_COMMAND_EXECUTION_FAILED
                }
            } catch (e: IOException) {
                return RESULT_NO_POWERSHELL_INSTALLED
            } catch (e: InterruptedException) {
                return RESULT_NO_POWERSHELL_INSTALLED
            }

            // Get result
            try {
                val error = ioStr(powerShell.errorStream).trim { it <= ' ' }
                if (!error.isEmpty()) {
                    throw PowerShellException(error)
                }
                ioStr(powerShell.inputStream).trim { it <= ' ' }
            } catch (e: IOException) {
                RESULT_COMMAND_SUCCEEDED_FAILED_TO_GET_RESULT
            }
        } else {
            try {
                // Create server
                //logger.debug("Creating PowerShell administrative server...");
                val server = ServerSocket(0)
                server.soTimeout = POWERSHELL_ADMINISTRATIVE_TIMEOUT
                var state = 0
                val password = Random().nextLong()
                val startTime = System.currentTimeMillis()
                //logger.debug("Created PowerShell administrative server  with password " + password + " on port "
                //	+ server.getLocalPort());

                // Create client process
                //logger.debug("Executing administrative PowerShell command...");
                var administrativeCommand = (POWERSHELL_EXECUTABLE
                        + " Start-Process -Verb runAs javaw.exe \'" + "-cp \"\$path\" "
                        + PowerShellAdministrativeClient::class.java.name + " " + server.localPort + " " + password
                        + " " + command + END_OF_TEXT + "\'")
                administrativeCommand = if (runningJarFile != null) {
                    administrativeCommand.replace(
                        "\$path",
                        runningJarFile!!.absolutePath
                    )
                } else {
                    administrativeCommand.replace(
                        "\$path",
                        runningLocation!!.absolutePath
                    )
                }
                val powerShell = Runtime.getRuntime().exec(administrativeCommand)
                powerShell.outputStream.close()
                powerShell.errorStream.close()
                powerShell.inputStream.close()
                powerShell.waitFor()
                if (powerShell.exitValue() != 0) {
                    //	logger.debug("Failed to execute administrative PowerShell command");
                    server.close()
                    return RESULT_COMMAND_EXECUTION_FAILED
                }
                //logger.debug("Executed administrative PowerShell command");

                // Wait for connection
                //logger.debug("Waiting for connection from administrative PowerShell client...");
                var connection: Socket? = null
                connection = try {
                    server.accept()
                } catch (e: IOException) {
                    server.close()
                    return RESULT_ADMINISTRATIVE_EXECUTION_FAILED
                }
                val connectionIn = DataInputStream(connection?.getInputStream() ?: TODO())
                val connectionOut = DataOutputStream(connection.getOutputStream())
                //logger.debug("Administrative PowerShell client connected, waiting for password...");
                while (System.currentTimeMillis() - startTime <= POWERSHELL_ADMINISTRATIVE_TIMEOUT) {
                    Thread.sleep(0, 1) // Lower CPU usage
                    if (state == STATE_AUTHENTICATION && connectionIn.available() >= java.lang.Long.BYTES) {
                        val givenPassword = connectionIn.readLong()
                        if (password == givenPassword) {
                            connectionOut.writeInt(AUTHENTICATION_SUCCESS)
                            connectionOut.flush()
                            state = STATE_ERROR_RESULT
                            //	logger.debug(
                            //		"Administrative PowerShell client has authenticated, waiting for error results...");
                        } else {
                            connectionOut.writeInt(AUTHENTICATION_FAILURE)
                            connectionOut.flush()
                            //	logger.error("Administrative PowerShell client failed to authenticate");
                        }
                    } else if (state == STATE_ERROR_RESULT && connectionIn.available() >= 3) {
                        val errorResponse = connectionIn.readUTF().trim { it <= ' ' }
                        if (errorResponse.length > 0) {
                            server.close()
                            return errorResponse
                        }
                        state = STATE_RESULT
                        //logger.debug("Administrative PowerShell client has sent error results, waiting for results...");
                    } else if (state == STATE_RESULT && connectionIn.available() >= 3) {
                        server.close()
                        return connectionIn.readUTF().trim { it <= ' ' }
                    }
                }
                connection.close()
                server.close()
                //logger.debug("Destroyed adminstrative PowerShell server");
                RESULT_ADMINISTRATIVE_EXECUTION_FAILED
            } catch (e: IOException) {
                RESULT_ADMINISTRATIVE_EXECUTION_FAILED
            } catch (e: InterruptedException) {
                RESULT_ADMINISTRATIVE_EXECUTION_FAILED
            }
        }
    }

    /**
     * Executes the command. Once the command has been executed, its arguments
     * will be cleared so they do not linger in the case the same command with
     * different arguments is executed.
     *
     *
     * Take note that a `PowerShellException` not being thrown is not
     * an indication that the command actually executed. Rather, it just means
     * that the execution of this method in particular did not fail. The main
     * case of this is being a command not being executed either because the
     * machine is not running on Windows 10 or that it requires elevation but
     * the user declined.
     *
     * @return the execution result.
     * @throws PowerShellException
     * if a PowerShell error occurs.
     */
    @Synchronized
    @Throws(PowerShellException::class)
    fun execute(): String {
        return this.execute(false)
    }

    override fun toString(): String {
        return "PowerShellCommand [command=$command]"
    }

    companion object {
        private const val POWERSHELL_EXECUTABLE = "powershell.exe"
        private val POWERSHELL_BASE64_CHARSET = Charset.forName("UTF-16LE")
        private const val END_OF_TEXT = 0x03.toChar()
        private const val POWERSHELL_ADMINISTRATIVE_TIMEOUT = 10000
        private const val AUTHENTICATION_FAILURE = 0x00
        private const val AUTHENTICATION_SUCCESS = 0x01
        private const val STATE_AUTHENTICATION = 0x00
        private const val STATE_ERROR_RESULT = 0x01
        private const val STATE_RESULT = 0x02

        /**
         * The argument prefix.
         */
        const val ARGUMENT_PREFIX = "$"

        /**
         * Command execution was successful.
         */
        const val RESULT_OK = "OK."

        /**
         * PowerShell is not installed on the host machine.
         */
        const val RESULT_NO_POWERSHELL_INSTALLED = "No PowerShell installed."

        /**
         * Command execution was a failure.
         */
        const val RESULT_COMMAND_EXECUTION_FAILED = "Command execution failed."

        /**
         * Command execution was successful, however getting the results was a
         * failure.
         */
        const val RESULT_COMMAND_SUCCEEDED_FAILED_TO_GET_RESULT = "Command succeeded, but failed to get result."

        /**
         * Executing with administrative privileges was a failure.
         */
        const val RESULT_ADMINISTRATIVE_EXECUTION_FAILED =
            "Failed to execute with administrative privileges."// Failed to determine location

        /**
         * Returns the location the program is being run at.
         *
         * @return the location the program is being run at, `null` if
         * determining the location was a failure.
         */
        private val runningLocation: File?
            private get() = try {
                File(PowerShellCommand::class.java.protectionDomain.codeSource.location.toURI())
            } catch (e: URISyntaxException) {
                null // Failed to determine location
            }// Not a JAR file

        /**
         * Returns the currently running JAR file.
         *
         * @return the currently running JAR file, `null` if the
         * application is not being run from a JAR.
         * @see .getRunningLocation
         */
        private val runningJarFile: File?
            private get() {
                val runningJar = runningLocation
                return if (runningJar!!.isDirectory || !runningJar.name.endsWith(".jar")) {
                    null // Not a JAR file
                } else runningJar
            }

        /**
         * Converts the specified [InputStream] to a string. This will result
         * in the closing of the stream, as all available data will be read from it
         * during conversion.
         *
         * @param in
         * the stream to convert.
         * @return the converted string.
         * @throws NullPointerException
         * if the `in` stream is `null`.
         * @throws IOException
         * if an I/O error occurs.
         */
        @Throws(NullPointerException::class, IOException::class)
        private fun ioStr(`in`: InputStream?): String {
            if (`in` == null) {
                throw NullPointerException("Input stream cannot be null")
            }
            var str = String()
            var next: String? = null
            val reader = BufferedReader(InputStreamReader(`in`))
            while (reader.readLine().also { next = it } != null) {
                str += """
                    $next
                    
                    """.trimIndent()
            }
            reader.close()
            return if (str.length > 1) {
                str.substring(0, str.length - 1)
            } else str
        }

        private const val commandIndex = 0
    }

    /**
     * Creates a PowerShell command that can be executed.
     *
     * @param command
     * the command string. To allow for the use of arguments, use
     * {@value #ARGUMENT_PREFIX} before the argument name.
     */
    init {
        //this.logger = LogManager.getLogger("PowerShellCommand-" + commandIndex++);
        arguments = HashMap()
    }
}