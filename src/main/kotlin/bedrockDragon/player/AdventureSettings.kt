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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bedrockDragon.player

import bedrockDragon.network.raknet.protocol.game.world.AdventureSettingsPacket

/**
 * [AdventureSettings] (NOT TO BE CONFUSED WITH ADVENTURE GAMEMODE) represents permissions a player has
 * on the server.
 * @author Nathan Swanson
 * @since ALPHA
 */
class AdventureSettings {

    enum class Type(val id: Int,val  defaultValue: Boolean) {
        WORLD_IMMUTABLE(AdventureSettingsPacket.WORLD_IMMUTABLE, false),
        AUTO_JUMP(AdventureSettingsPacket.AUTO_JUMP, true),
        ALLOW_FLIGHT(AdventureSettingsPacket.ALLOW_FLIGHT, false),
        NO_CLIP(AdventureSettingsPacket.NO_CLIP, false),
        //WORLD_BUILDER(AdventureSettingsPacket.WORLD_BUILDER, true),
        FLYING(AdventureSettingsPacket.FLYING, false),
        NO_PVP(AdventureSettingsPacket.NO_PVP, false)
        //MUTED(AdventureSettingsPacket.MUTED, false),
        //BUILD_AND_MINE(AdventureSettingsPacket.BUILD, true),
        //DOORS_AND_SWITCHED(AdventureSettingsPacket.DOORS_AND_SWITCHES, true),
        //OPEN_CONTAINERS(AdventureSettingsPacket.OPEN_CONTAINERS, true),
        //ATTACK_PLAYERS(AdventureSettingsPacket.ATTACK_PLAYERS, true),
        //ATTACK_MOBS(AdventureSettingsPacket.ATTACK_MOBS, true),
        //OPERATOR(AdventureSettingsPacket.OPERATOR, false),
        //TELEPORT(AdventureSettingsPacket.TELEPORT, false);
    }
}