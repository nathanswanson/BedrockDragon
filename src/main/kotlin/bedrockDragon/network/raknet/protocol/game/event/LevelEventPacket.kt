package bedrockDragon.network.raknet.protocol.game.event

import bedrockDragon.network.raknet.protocol.game.MinecraftPacketConstants
import bedrockDragon.network.raknet.protocol.game.PacketPayload
import dev.romainguy.kotlin.math.Float3

class LevelEventPacket: PacketPayload(MinecraftPacketConstants.LEVEL_EVENT) {
    companion object {
        val EVENT_SOUND_CLICK = 1000
        val EVENT_SOUND_CLICK_FAIL = 1001
        val EVENT_SOUND_SHOOT = 1002
        val EVENT_SOUND_DOOR = 1003
        val EVENT_SOUND_FIZZ = 1004
        val EVENT_SOUND_TNT = 1005

        val EVENT_SOUND_GHAST = 1007
        val EVENT_SOUND_BLAZE_SHOOT = 1008
        val EVENT_SOUND_GHAST_SHOOT = 1009
        val EVENT_SOUND_DOOR_BUMP = 1010
        val EVENT_SOUND_DOOR_CRASH = 1012

        val EVENT_SOUND_ENDERMAN_TELEPORT = 1018

        val EVENT_SOUND_ANVIL_BREAK = 1020
        val EVENT_SOUND_ANVIL_USE = 1021
        val EVENT_SOUND_ANVIL_FALL = 1022

        val EVENT_SOUND_ITEM_DROP = 1030
        val EVENT_SOUND_ITEM_THROWN = 1031

        val EVENT_SOUND_PORTAL = 1032

        val EVENT_SOUND_ITEM_FRAME_ITEM_ADDED = 1040
        val EVENT_SOUND_ITEM_FRAME_PLACED = 1041
        val EVENT_SOUND_ITEM_FRAME_REMOVED = 1042
        val EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED = 1043
        val EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED = 1044

        val EVENT_SOUND_CAMERA_TAKE_PICTURE = 1050
        val EVENT_SOUND_EXPERIENCE_ORB = 1051
        val EVENT_SOUND_TOTEM = 1052

        val EVENT_SOUND_ARMOR_STAND_BREAK = 1060
        val EVENT_SOUND_ARMOR_STAND_HIT = 1061
        val EVENT_SOUND_ARMOR_STAND_FALL = 1062
        val EVENT_SOUND_ARMOR_STAND_PLACE = 1063

        val EVENT_GUARDIAN_CURSE = 2006

        val EVENT_PARTICLE_BLOCK_FORCE_FIELD = 2008
        val EVENT_PARTICLE_PROJECTILE_HIT = 2009
        val EVENT_PARTICLE_DRAGON_EGG_TELEPORT = 2010

        val EVENT_PARTICLE_ENDERMAN_TELEPORT = 2013
        val EVENT_PARTICLE_PUNCH_BLOCK = 2014

        val EVENT_SOUND_BUTTON_CLICK = 3500
        val EVENT_SOUND_EXPLODE = 3501
        val EVENT_CAULDRON_DYE_ARMOR = 3502
        val EVENT_CAULDRON_CLEAN_ARMOR = 3503
        val EVENT_CAULDRON_FILL_POTION = 3504
        val EVENT_CAULDRON_TAKE_POTION = 3505
        val EVENT_SOUND_SPLASH = 3506
        val EVENT_CAULDRON_TAKE_WATER = 3507
        val EVENT_CAULDRON_ADD_DYE = 3508
        val EVENT_CAULDRON_CLEAN_BANNER = 3509

        val EVENT_PARTICLE_SHOOT = 2000
        val EVENT_PARTICLE_DESTROY = 2001
        val EVENT_PARTICLE_SPLASH = 2002
        val EVENT_PARTICLE_EYE_DESPAWN = 2003
        val EVENT_PARTICLE_SPAWN = 2004
        val EVENT_PARTICLE_BONEMEAL = 2005

        val EVENT_START_RAIN = 3001
        val EVENT_START_THUNDER = 3002
        val EVENT_STOP_RAIN = 3003
        val EVENT_STOP_THUNDER = 3004

        val EVENT_SOUND_CAULDRON = 3501
        val EVENT_SOUND_CAULDRON_DYE_ARMOR = 3502
        val EVENT_SOUND_CAULDRON_FILL_POTION = 3504
        val EVENT_SOUND_CAULDRON_FILL_WATER = 3506

        val EVENT_BLOCK_START_BREAK = 3600
        val EVENT_BLOCK_STOP_BREAK = 3601

        val EVENT_SET_DATA = 4000

        val EVENT_PLAYERS_SLEEPING = 9800

        val EVENT_ADD_PARTICLE_MASK = 0x4000
    }


    var eventId = 0 //sVarInt
    lateinit var position: Float3
    var data = 0 //sVarint

    override suspend fun encode() {
        writeVarInt(eventId)
        writeVector3(position)
        write(data)
    }
}