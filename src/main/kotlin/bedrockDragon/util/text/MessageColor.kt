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
 * Copyright (c) 2021-2022 Nathan Swanson
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * @author //https://github.com/importre/crayon/blob/master/src/main/kotlin/com/importre/crayon/Crayon.kt
 */
package bedrockDragon.util.text

fun String.bold() = "\u001B$bold${this}$reset"
fun String.italic() = "\u001B$italic${this}$reset"
fun String.underline() = "\u001B$underline${this}$reset"
fun String.reversed() = "\u001B$reversed${this}$reset"
fun String.black() = "\u001b$black${this}$reset"
fun String.blue() = "\u001b$blue${this}$reset"
fun String.cyan() = "\u001b$cyan${this}$reset"
fun String.green() = "\u001b$green${this}$reset"
fun String.magenta() = "\u001b$magenta${this}$reset"
fun String.red() = "\u001b$red${this}$reset"
fun String.white() = "\u001b$white${this}$reset"
fun String.yellow() = MineText("\u001b$yellow${this}$reset", MineText.Type.CONSOLE)
fun String.brightBlack() = "\u001b$brightBlack${this}$reset"
fun String.brightBlue() = "\u001b$brightBlue${this}$reset"
fun String.brightCyan() = "\u001b$brightCyan${this}$reset"
fun String.brightGreen() = "\u001b$brightGreen${this}$reset"
fun String.brightMagenta() = "\u001b$brightMagenta${this}$reset"
fun String.brightRed() = "\u001b$brightRed${this}$reset"
fun String.brightWhite() = "\u001b$brightWhite${this}$reset"
fun String.brightYellow() = "\u001b$brightYellow${this}$reset"
fun String.bgBlack() = "\u001b$bgBlack${this}$reset"
fun String.bgBlue() = "\u001b$bgBlue${this}$reset"
fun String.bgCyan() = "\u001b$bgCyan${this}$reset"
fun String.bgGreen() = "\u001b$bgGreen${this}$reset"
fun String.bgMagenta() = "\u001b$bgMagenta${this}$reset"
fun String.bgRed() = "\u001b$bgRed${this}$reset"
fun String.bgWhite() = "\u001b$bgWhite${this}$reset"
fun String.bgYellow() = "\u001b$bgYellow${this}$reset"
fun String.bgBrightBlack() = "\u001b$bgBrightBlack${this}$reset"
fun String.bgBrightBlue() = "\u001b$bgBrightBlue${this}$reset"
fun String.bgBrightCyan() = "\u001b$bgBrightCyan${this}$reset"
fun String.bgBrightGreen() = "\u001b$bgBrightGreen${this}$reset"
fun String.bgBrightMagenta() = "\u001b$bgBrightMagenta${this}$reset"
fun String.bgBrightRed() = "\u001b$bgBrightRed${this}$reset"
fun String.bgBrightWhite() = "\u001b$bgBrightWhite${this}$reset"
fun String.bgBrightYellow() = "\u001b$bgBrightYellow${this}$reset"

fun Char.bold() = "\u001B$bold${this}$reset"
fun Char.italic() = "\u001B$italic${this}$reset"
fun Char.underline() = "\u001B$underline${this}$reset"
fun Char.reversed() = "\u001B$reversed${this}$reset"
fun Char.black() = "\u001b$black${this}$reset"
fun Char.blue() = "\u001b$blue${this}$reset"
fun Char.cyan() = "\u001b$cyan${this}$reset"
fun Char.green() = "\u001b$green${this}$reset"
fun Char.magenta() = "\u001b$magenta${this}$reset"
fun Char.red() = "\u001b$red${this}$reset"
fun Char.white() = "\u001b$white${this}$reset"
fun Char.yellow() = "\u001b$yellow${this}$reset"
fun Char.brightBlack() = "\u001b$brightBlack${this}$reset"
fun Char.brightBlue() = "\u001b$brightBlue${this}$reset"
fun Char.brightCyan() = "\u001b$brightCyan${this}$reset"
fun Char.brightGreen() = "\u001b$brightGreen${this}$reset"
fun Char.brightMagenta() = "\u001b$brightMagenta${this}$reset"
fun Char.brightRed() = "\u001b$brightRed${this}$reset"
fun Char.brightWhite() = "\u001b$brightWhite${this}$reset"
fun Char.brightYellow() = "\u001b$brightYellow${this}$reset"
fun Char.bgBlack() = "\u001b$bgBlack${this}$reset"
fun Char.bgBlue() = "\u001b$bgBlue${this}$reset"
fun Char.bgCyan() = "\u001b$bgCyan${this}$reset"
fun Char.bgGreen() = "\u001b$bgGreen${this}$reset"
fun Char.bgMagenta() = "\u001b$bgMagenta${this}$reset"
fun Char.bgRed() = "\u001b$bgRed${this}$reset"
fun Char.bgWhite() = "\u001b$bgWhite${this}$reset"
fun Char.bgYellow() = "\u001b$bgYellow${this}$reset"
fun Char.bgBrightBlack() = "\u001b$bgBrightBlack${this}$reset"
fun Char.bgBrightBlue() = "\u001b$bgBrightBlue${this}$reset"
fun Char.bgBrightCyan() = "\u001b$bgBrightCyan${this}$reset"
fun Char.bgBrightGreen() = "\u001b$bgBrightGreen${this}$reset"
fun Char.bgBrightMagenta() = "\u001b$bgBrightMagenta${this}$reset"
fun Char.bgBrightRed() = "\u001b$bgBrightRed${this}$reset"
fun Char.bgBrightWhite() = "\u001b$bgBrightWhite${this}$reset"
fun Char.bgBrightYellow() = "\u001b$bgBrightYellow${this}$reset"

const val reset = "[0m"
const val bold = "[1m"
const val italic = "[3m"
const val underline = "[4m"
const val reversed = "[7m"
const val black = "[30m"
const val blue = "[34m"
const val cyan = "[36m"
const val green = "[32m"
const val magenta = "[35m"
const val red = "[31m"
const val white = "[37m"
const val yellow = "[33m"
const val brightBlack = "[30;1m"
const val brightBlue = "[34;1m"
const val brightCyan = "[36;1m"
const val brightGreen = "[32;1m"
const val brightMagenta = "[35;1m"
const val brightRed = "[31;1m"
const val brightWhite = "[37;1m"
const val brightYellow = "[33;1m"
const val bgBlack = "[40m"
const val bgBlue = "[44m"
const val bgCyan = "[46m"
const val bgGreen = "[42m"
const val bgMagenta = "[45m"
const val bgRed = "[41m"
const val bgWhite = "[47m"
const val bgYellow = "[43m"
const val bgBrightBlack = "[40;1m"
const val bgBrightBlue = "[44;1m"
const val bgBrightCyan = "[46;1m"
const val bgBrightGreen = "[42;1m"
const val bgBrightMagenta = "[45;1m"
const val bgBrightRed = "[41;1m"
const val bgBrightWhite = "[47;1m"
const val bgBrightYellow = "[43;1m"