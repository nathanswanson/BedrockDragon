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

package bedrockDragon.network.raknet.protocol.game

object MinecraftPacketConstants {
    const val LOGIN = 0x01
    const val PLAY_STATUS = 0x02
    const val SERVER_TO_CLIENT_HANDSHAKE = 0x03
    const val CLIENT_TO_SERVER_HANDSHAKE = 0x04
    const val DISCONNECT = 0x05
    const val RESOURCE_PACKS_INFO = 0x06
    const val RESOURCE_PACK_STACK = 0x07
    const val RESOURCE_PACK_RESPONSE = 0x08
    const val TEXT = 0x09
    const val SET_TIME = 0x0a
    const val START_GAME = 0x0b
    const val ADD_PLAYER = 0x0c
    const val ADD_ENTITY = 0x0d
    const val REMOVE_ENTITY = 0x0e
    const val ADD_ITEM_ENTITY = 0x0f
    const val TAKE_ITEM_ENTITY = 0x11
    const val MOVE_ENTITY_ABSOLUTE = 0x12
    const val MOVE_PLAYER = 0x13
    const val RIDER_JUMP = 0x14
    const val UPDATE_BLOCK = 0x15
    const val ADD_PAINTING = 0x16
    const val TICK_SYNC = 0x17
    const val LEVEL_SOUND_EVENT_ONE = 0x18 //marked as old - investigate
    const val LEVEL_EVENT = 0x19
    const val BLOCK_EVENT = 0x1a
    const val ENTITY_EVENT = 0x1b
    const val MOB_EFFECT = 0x1c
    const val UPDATE_ATTRIBUTES = 0x1d
    const val INVENTORY_TRANSACTION = 0x1e
    const val MOB_EQUIPMENT = 0x1f
    const val MOB_ARMOR_EQUIPMENT = 0x20
    const val INTERACT = 0x21
    const val BLOCK_PICK_REQUEST = 0x22
    const val ENTITY_PICK_REQUEST = 0x23
    const val PLAYER_ACTION = 0x24
    const val ENTITY_FALL = 0x25
    const val HURT_ARMOR = 0x26
    const val SET_ENTITY_DATA = 0x27
    const val SET_ENTITY_MOTION = 0x28
    const val SET_ENTITY_LINK = 0x29
    const val SET_HEALTH = 0x2a
    const val SET_SPAWN_POSITION = 0x2b
    const val ANIMATE = 0x2c
    const val RESPAWN = 0x2d
    const val CONTAINER_OPEN = 0x2e
    const val CONTAINER_CLOSE = 0x2f
    const val PLAYER_HOTBAR = 0x30
    const val INVENTORY_CONTENT = 0x31
    const val INVENTORY_SLOT = 0x32
    const val CONTAINER_SET_DATA = 0x33
    const val CRAFTING_DATA = 0x34
    const val CRAFTING_EVENT = 0x35
    const val GUI_DATA_PICK_ITEM = 0x36
    const val ADVENTURE_SETTINGS = 0x37
    const val BLOCK_ENTITY_DATA = 0x38
    const val PLAYER_INPUT = 0x39
    const val LEVEL_CHUNK = 0x3a
    const val SET_COMMANDS_ENABLED = 0x3b
    const val SET_DIFFICULTY = 0x3c
    const val CHANGED_DIMENSION = 0x3d
    const val SET_PLAYER_GAME_TYPE = 0x3e
    const val PLAYER_LIST = 0x3f
    const val SIMPLE_EVENT = 0x40
    const val EVENT = 0x41
    const val SPAWN_EXPERIENCE_ORB = 0x42
    const val MAP_ITEM_DATA = 0x43
    const val MAP_INFO_REQUEST = 0x44
    const val REQUEST_CHUNK_RADIUS = 0x45
    const val CHUNK_RADIUS_UPDATED = 0x46
    const val ITEMFRAME_DROP_ITEM = 0x47
    const val GAME_RULES_CHANGED = 0x48
    const val CAMERA = 0x49
    const val BOSS_EVENT = 0x4a
    const val SHOW_CREDITS = 0x4b
    const val AVAILABLE_COMMANDS = 0x4c
    const val COMMAND_REQUEST = 0x4d
    const val COMMAND_BLOCK_UPDATE = 0x4e
    const val COMMAND_OUTPUT = 0x4f
    const val UPDATE_TRADE = 0x50
    const val UPDATE_EQUIP = 0x51
    const val RESOURCE_PACK_DATA_INFO = 0x52
    const val RESOURCE_PACK_CHUNK_DATA = 0x53
    const val RESOURCE_PACK_CHUNK_REQUEST = 0x54
    const val TRANSFER = 0x55
    const val PLAY_SOUND = 0x56
    const val STOP_SOUND = 0x57
    const val SET_TITLE = 0x58
    const val ADD_BEHAVIOR_TREE = 0x59
    const val STRUCTURE_BLOCK_UPDATE = 0x5a
    const val SHOW_STORE_OFFER = 0x5b
    const val PURCHASE_RECEIPT = 0x5c
    const val PLAYER_SKIN = 0x5d
    const val SUB_CLIENT_LOGIN = 0x5e
    const val AUTOMATION_CLIENT_CONNECT = 0x5f
    const val SET_LAST_HURT_BY = 0x60
    const val BOOK_EDIT = 0x61
    const val NPC_REQUEST = 0x62
    const val PHOTO_TRANSFER = 0x63
    const val MODEL_FORM_REQUEST = 0x64
    const val MODEL_FORM_RESPONSE = 0x65
    const val SERVER_SETTINGS_REQUEST = 0x66
    const val SERVER_SETTINGS_RESPONSE = 0x67
    const val SHOW_PROFILE = 0x68
    const val SET_DEFAULT_GAME_TYPE = 0x69
    const val REMOVE_OBJECTIVE = 0x6a
    const val SET_DISPLAY_OBJECTIVE = 0x6b
    const val SET_SCORE = 0x6c
    const val LAB_TABLE = 0x6d
    const val UPDATE_BLOCK_SYNCED = 0x6e
    const val MOVE_ENTITY_DELTA = 0x6f
    const val SET_SCOREBOARD_IDENTITY = 0x70
    const val SET_LOCAL_PLAYER_AS_INITIALIZED = 0X71
    const val UPDATE_SOFT_ENUM = 0x72
    const val NETWORK_STACK_LATENCY = 0x73
    const val SCRIPT_CUSTOM_EVENT = 0x75
    const val SPAWN_PARTICLE_EFFECT = 0x76
    const val AVAILABLE_ENTITY_IDENTIFIERS = 0x77
    const val LEVEL_SOUND_EVENT_TWO = 0x78
    const val NETWORK_CHUNK_PUBLISHER_UPDATE = 0x79
    const val BIOME_DEFINITION_LIST = 0x7A
    const val LEVEL_SOUND_EVENT_THREE = 0x7b
    const val LEVEL_EVENT_GENERIC = 0x7c
    const val LECTERN_UPDATE = 0x7d
    const val VIDEO_STREAM_CONNECT = 0x7e

    const val ADD_ECS_ENTITY = 0x7f
    const val REMOVE_ECS_ENTITY = 0x80

    const val CLIENT_CACHE_STATUS = 0x81
    const val ON_SCREEN_TEXTURE_ANIMATION = 0x82
    const val MAP_CREATE_LOCKED_COPY = 0x83
    const val STRUCTURE_TEMPLATE_DATA_EXPORT_REQUEST = 0x84
    const val STRUCTURE_TEMPLATE_DATA_EXPORT_RESPONSE = 0x85
    const val UPDATE_BLOCK_PROPERTIES = 0x86
    const val CLIENT_CACHE_BLOB_STATUS = 0x87
    const val CLIENT_CACHE_MISS_RESPONSE = 0x88

    const val EDUCATION_SETTINGS = 0x89
    const val MULTIPLAYER_SETTINGS = 0x8b
    const val SETTINGS_COMMAND = 0x8c
    const val ANVIL_DAMAGE = 0x8d
    const val COMPLETED_USING_ITEM = 0x8e

    const val NETWORK_SETTINGS = 0x8f
    const val PLAYER_AUTH_INPUT = 0x90
    const val CREATIVE_CONTENT = 0x91
    const val PLAYER_ENCHANT_OPTIONS = 0x92
    const val ITEM_STACK_REQUEST = 0x93
    const val ITEM_STACK_RESPONSE = 0x94
    const val PLAYER_ARMOR_DAMAGE = 0x95
    const val CODE_BUILDER = 0x96
    const val UPDATE_PLAYER_GAME_TYPE = 0x97
    const val EMOTE_LIST = 0x98
    const val POSITION_TRACKING_DB_SERVER_BROADCAST = 0x99
    const val POSITION_TRACKING_DB_CLIENT_REQUEST = 0x9a
    const val DEBUG_INFO = 0x9b
    const val MALFORM_PACKET = 0x9c

    const val MOTION_PREDICTION_HINTS = 0x9d

    const val ANIMATE_ENTITY = 0x9e

    const val CAMERA_SHAKE = 0x9f
    const val PLAYER_FOG = 0xa0
    const val CORRECT_PLAYER_MOVE_PREDICTION = 0xa1

    const val ITEM_COMPONENT = 0xa2
    const val FILTER_TEXT = 0xa3

    const val DEBUG_RENDERER = 0xa4
    const val SYNC_ENTITY_PROPERTY = 0xa5
    const val ADD_VOLUME_ENTITY = 0xa6
    const val REMOVE_VOLUME_ENTITY = 0xa7
    const val SIMULATION_TYPE = 0xa8
    const val NPC_DIALOGUE = 0xa9
    const val EDU_URI_RESOURCE_PACKET = 0xaa
    const val CREATE_PHOTO = 0xab
    const val UPDATE_SUBCHUNK_BLOCKS = 0xac
    const val PHOTO_INFO_REQUEST = 0xad
    const val SUBCHUNK = 0xae
    const val SUBCHUNK_REQUEST = 0xaf
}