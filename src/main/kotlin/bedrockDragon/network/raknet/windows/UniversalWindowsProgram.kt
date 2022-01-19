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

import bedrockDragon.network.raknet.windows.PowerShellException
import java.util.*

/**
 * A universal Windows program.
 *
 *
 * Mainly meant to be used to give universal Windows programs loopback exemption
 * so users can connect to JRakNet servers on the same machine. This class can
 * safely be used on other machines that are not running on the Windows 10
 * operating system without risking crashes due to incompatibilities. However,
 * if the machine is not running Windows 10 then this class is guaranteed to
 * behave differently with code intentionally not running or giving different
 * results.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.10.0
 */
class UniversalWindowsProgram
/**
 * Creates a Universal Windows Program.
 *
 * @param applicationId
 * the application ID.
 */(
    /**
     * Returns the application ID.
     *
     * @return the application ID.
     */
    val applicationId: String
) {

    /**
     * Returns whether or not the application is loopback exempt.
     *
     *
     * The term "loopback exempt" means that an application is exempt from the
     * rule that it cannot connect to a server running on the same machine as it
     * is.
     *
     * @return `true` if the application is loopback exempt,
     * `false` otherwise.
     * @throws PowerShellException
     * if a PowerShell error occurs.
     */
    @get:Throws(PowerShellException::class)
    val isLoopbackExempt: Boolean
        get() = if (!isWindows10) {
            true // Already exempted on non-Windows 10 machine
        } else CHECKNETISOLATION_LOOPBACKEXEMPT_SHOW.execute().toLowerCase()
            .contains(applicationId.toLowerCase())

    /**
     * Sets whether or not the application is loopback exempt.
     *
     *
     * The term "loopback exempt" means that an application is exempt from the
     * rule that it cannot connect to a server running on the same machine as it
     * is.
     *
     * @param exempt
     * `true` if the application is loopback exempt,
     * `false` otherwise.
     * @return `true` if making the application loopback exempt was
     * successful, `false` otherwise. A success means that
     * the machine is not running on Windows 10 (no code needed to be
     * executed), the exemption status was successfully changed, or that
     * the `exempt` value is already what is now.
     * @throws PowerShellException
     * if a PowerShell error occurs.
     */
    @Throws(PowerShellException::class)
    fun setLoopbackExempt(exempt: Boolean): Boolean {
        if (!isWindows10) {
            return true // Not running on Windows 10
        }
        val exempted = isLoopbackExempt
        if (exempt == true && exempted == false) {
            return CHECKNETISOLATION_LOOPBACKEXEMPT_ADD.setArgument(APPLICATION_ARGUMENT, applicationId)
                .execute(true) == PowerShellCommand.Companion.RESULT_OK
        } else if (exempt == false && exempted == true) {
            return CHECKNETISOLATION_LOOPBACKEXEMPT_DELETE.setArgument(APPLICATION_ARGUMENT, applicationId)
                .execute(true) == PowerShellCommand.Companion.RESULT_OK
        }
        return true // No operation executed
    }

    override fun hashCode(): Int {
        return Objects.hash(applicationId)
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        } else if (o !is UniversalWindowsProgram) {
            return false
        }
        return applicationId == o.applicationId
    }

    override fun toString(): String {
        return "UniversalWindowsProgram [applicationId=$applicationId]"
    }

    companion object {
        /**
         * The Minecraft Universal Windows Program.
         */
        val MINECRAFT = UniversalWindowsProgram(
            "Microsoft.MinecraftUWP_8wekyb3d8bbwe"
        )
        private val APPLICATION_ARGUMENT: String = PowerShellCommand.Companion.ARGUMENT_PREFIX + "application"
        private val CHECKNETISOLATION_LOOPBACKEXEMPT_ADD = PowerShellCommand(
            "CheckNetIsolation LoopbackExempt -a -n=\"" + APPLICATION_ARGUMENT + "\""
        )
        private val CHECKNETISOLATION_LOOPBACKEXEMPT_DELETE = PowerShellCommand(
            "CheckNetIsolation LoopbackExempt -d -n=\"" + APPLICATION_ARGUMENT + "\""
        )
        private val CHECKNETISOLATION_LOOPBACKEXEMPT_SHOW = PowerShellCommand(
            "CheckNetIsolation LoopbackExempt -s"
        )

        /**
         * Returns whether or not the machine is currently running on the Windows 10
         * operating system.
         *
         * @return `true` if the machine is currently running on the
         * Windows 10 operating system, `false` otherwise.
         */
        val isWindows10: Boolean
            get() = System.getProperty("os.name").equals("Windows 10", ignoreCase = true)
    }
}