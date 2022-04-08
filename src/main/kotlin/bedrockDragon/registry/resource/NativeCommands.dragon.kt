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

package bedrockDragon.registry.resource

import bedrockDragon.command.CommandTag
import bedrockDragon.command.registerCommand
import bedrockDragon.player.Player
import bedrockDragon.registry.Registry
import dev.romainguy.kotlin.math.Float3

object NativeCommands {
    init {
        registerCommand("bedrockDragon") {

            command("/gamemode") {

                args.add(CommandTag.commandIntTag()) //gamemode value
                args.add(CommandTag.commandStringTag().asOptional()) //target (@s if none)


                invoke = {
                    player, anies ->
                    player.gamemode = Player.Gamemode.values()[(anies[0] as String).toInt()]
                }
            }
            command("/tp") {
                args.add(CommandTag.commandIntTag()) //x
                args.add(CommandTag.commandIntTag()) //y
                args.add(CommandTag.commandIntTag()) //z

                args.add(CommandTag.commandStringTag().asOptional()) //target (@s if none)

                invoke = {
                    player, anies ->
                        player.teleport(Float3(
                            (anies[0] as String).toFloat(),
                            (anies[1] as String).toFloat(),
                            (anies[2] as String).toFloat()
                        ))
                }
            }
            command("/give") {
                args.add(CommandTag.commandStringTag()) //target
                args.add(CommandTag.commandStringTag()) //item name
                args.add(CommandTag.commandIntTag().asDefault(1).asOptional()) // amount
                args.add(CommandTag.commandIntTag().asOptional()) //data Int
                args.add(CommandTag.commandStringTag().asOptional()) //components json

                invoke = {
                    player, anies ->
                    Registry.ITEM_REGISTRY[anies[1] as String]?.let {
                        it.count = (anies[2] as String).toInt()
                        player.addItem(it)
                    }
                }
            }
            command("/damage") {
                args.add(CommandTag.commandStringTag())
                args.add(CommandTag.commandIntTag())
                //damagecause

                invoke = {
                    player, anies ->
                    player.damage((anies[1] as String).toFloat())
                }
            }
            command("/kill") {
                args.add(CommandTag.commandStringTag().asOptional())

                invoke = {
                    player, anies ->
                        player.kill()
                }
            }
            command("/summon") {
                args.add(CommandTag.commandStringTag()) //mob name
                args.add(CommandTag.commandIntTag()) //x
                args.add(CommandTag.commandIntTag()) //y
                args.add(CommandTag.commandIntTag()) //z

                invoke = {
                    player, anies ->
                        player.world.spawnEntity(Float3((anies[0] as String).toFloat(), (anies[1] as String).toFloat(), (anies[2] as String).toFloat()), anies[3] as String, player)
                }
            }
            command("/kick") {
                invoke = {
                    player, anies ->
                        player.disconnect("You have been kicked from the Server.")
                }
            }
        }
    }
}