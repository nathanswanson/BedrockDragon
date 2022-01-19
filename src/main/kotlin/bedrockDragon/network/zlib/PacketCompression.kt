/*
 *      ##### ##                  ##                                    /                 ##### ##
 *   ######  /##                   ##                                 #/               /#####  /##
 *  /#   /  / ##                   ##                                 ##             //    /  / ###
 * /    /  /  ##                   ##                                 ##            /     /  /   ###
 *     /  /   /                    ##                                 ##                 /  /     ###
 *    ## ##  /        /##      ### ##  ###  /###     /###     /###    ##  /##           ## ##      ## ###  /###     /###     /###      /###   ###  /###
 *    ## ## /        / ###    ######### ###/ #### / / ###  / / ###  / ## / ###          ## ##      ##  ###/ #### / / ###  / /  ###  / / ###  / ###/ #### /
 *    ## ##/        /   ###  ##   ####   ##   ###/ /   ###/ /   ###/  ##/   /           ## ##      ##   ##   ###/ /   ###/ /    ###/ /   ###/   ##   ###/
 *    ## ## ###    ##    ### ##    ##    ##       ##    ## ##         ##   /            ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    ## ##   ###  ########  ##    ##    ##       ##    ## ##         ##  /             ## ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *    #  ##     ## #######   ##    ##    ##       ##    ## ##         ## ##             #  ##      ##   ##       ##    ## ##     ## ##    ##    ##    ##
 *       /      ## ##        ##    ##    ##       ##    ## ##         ######               /       /    ##       ##    ## ##     ## ##    ##    ##    ##
 *   /##/     ###  ####    / ##    /#    ##       ##    ## ###     /  ##  ###         /###/       /     ##       ##    /# ##     ## ##    ##    ##    ##
 *  /  ########     ######/   ####/      ###       ######   ######/   ##   ### /     /   ########/      ###       ####/ ## ########  ######     ###   ###
 * /     ####        #####     ###        ###       ####     #####     ##   ##/     /       ####         ###       ###   ##  ### ###  ####       ###   ###
 * #                                                                                #                                             ###
 *  ##                                                                               ##                                     ####   ###
 *                                                                                                                        /######  /#
 *                                                                                                                       /     ###/
 * the MIT License (MIT)
 *
 * Copyright (c) 2021-2021 Nathan Swanson
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
package bedrockDragon.network.zlib

import it.unimi.dsi.fastutil.bytes.ByteArrays
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream
import java.io.IOException
import java.util.zip.Deflater
import java.util.zip.Inflater

object PacketCompression {

    fun decompress(buffer: ByteArray): ByteArray {
        val inflater = Inflater(true)

        try {
            inflater.setInput(buffer)
            inflater.finished()

            val bos = FastByteArrayOutputStream(1024)
            bos.reset()

            val tempBytes = ByteArray(2 * 1024 * 1024)

            while(!inflater.finished()) {
                val i = inflater.inflate(tempBytes)
                if (i == 0) {
                    throw IOException("Needs dictionary: ${inflater.needsDictionary()} or input: ${inflater.needsInput()}")
                }

                bos.write(tempBytes,0, i)
            }

            return bos.array

        } finally {
            inflater.reset()
        }
    }

    fun compress(buffer: ByteArray, level: Int): ByteArray {
        val deflator = Deflater(7, true)

        return try {
            deflator.setLevel(level)
            deflator.setInput(buffer)
            deflator.finish()

            val bos = FastByteArrayOutputStream(1024)
            bos.reset()
            val tempBytes = ByteArray(2 * 1024 * 1024)
            while (!deflator.finished()) {
                val i = deflator.deflate(tempBytes)
                bos.write(tempBytes, 0, i)
            }
            bos.array
            //TODO make more efficent
            ByteArrays.copy(bos.array, 0, bos.position().toInt())
        } finally {
            deflator.reset()
        }
    }

}