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
import java.io.IOException
import java.io.BufferedReader
import kotlin.jvm.JvmStatic
import java.lang.StringBuilder
import bedrockDragon.network.raknet.windows.PowerShellAdministrativeClient
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Process
import java.util.HashMap
import java.lang.IllegalArgumentException
import bedrockDragon.network.raknet.windows.PowerShellCommand
import kotlin.jvm.Synchronized
import bedrockDragon.network.raknet.windows.PowerShellException
import java.lang.InterruptedException
import java.net.ServerSocket
import java.nio.charset.Charset
import java.io.File
import java.net.URISyntaxException
import java.lang.NullPointerException
import java.lang.RuntimeException
import bedrockDragon.network.raknet.windows.UniversalWindowsProgram
import java.util.Objects

/**
 * Signals that an error has occurred when executing a Windows PowerShell
 * command.
 *
 * @author "Whirvis" Trent Summerlin
 * @since JRakNet v2.10.0
 */
class PowerShellException
/**
 * Constructs a `PowerShellException`.
 *
 * @param message
 * the detail message.
 */
    (message: String?) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = 1662306011110232452L
    }
}