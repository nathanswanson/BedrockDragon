package bedrockDragon.entity

object DataTag {
    // Flags
    const val DATA_FLAG_ONFIRE = 0
    const val DATA_FLAG_SNEAKING = 1
    const val DATA_FLAG_RIDING = 2
    const val DATA_FLAG_SPRINTING = 3
    const val DATA_FLAG_ACTION = 4
    const val DATA_FLAG_INVISIBLE = 5
    const val DATA_FLAG_TEMPTED = 6
    const val DATA_FLAG_INLOVE = 7
    const val DATA_FLAG_SADDLED = 8
    const val DATA_FLAG_POWERED = 9
    const val DATA_FLAG_IGNITED = 10
    const val DATA_FLAG_BABY = 11 //disable head scaling


    const val DATA_FLAGS = 0
    const val DATA_HEALTH = 1 //int (minecart/boat)

    const val DATA_VARIANT = 2 //int

    const val DATA_COLOR = 3
    const val DATA_COLOUR = 3 //byte

    const val DATA_NAMETAG = 4 //string

    const val DATA_OWNER_EID = 5 //long

    const val DATA_TARGET_EID = 6 //long

    const val DATA_AIR = 7 //short

    const val DATA_POTION_COLOR = 8 //int (ARGB!)

    const val DATA_POTION_AMBIENT = 9 //byte

    const val DATA_JUMP_DURATION = 10 //long

    const val DATA_HURT_TIME = 11 //int (minecart/boat)

    const val DATA_HURT_DIRECTION = 12 //int (minecart/boat)

    const val DATA_PADDLE_TIME_LEFT = 13 //float

    const val DATA_PADDLE_TIME_RIGHT = 14 //float

    const val DATA_EXPERIENCE_VALUE = 15 //int (xp orb)

    const val DATA_DISPLAY_ITEM = 16 //int (id | (data << 16))

    const val DATA_DISPLAY_OFFSET = 17 //int

    const val DATA_HAS_DISPLAY = 18 //byte (must be 1 for minecart to show block inside)

    const val DATA_SWELL = 19
    const val DATA_OLD_SWELL = 20
    const val DATA_SWELL_DIR = 21
    const val DATA_CHARGE_AMOUNT = 22
    const val DATA_ENDERMAN_HELD_RUNTIME_ID = 23 //short

    const val DATA_ENTITY_AGE = 24 //short

    const val DATA_PLAYER_FLAGS = 26 //byte

    const val DATA_PLAYER_INDEX = 27
    const val DATA_PLAYER_BED_POSITION = 28 //block coords

    const val DATA_FIREBALL_POWER_X = 29 //float

    const val DATA_FIREBALL_POWER_Y = 30
    const val DATA_FIREBALL_POWER_Z = 31
    const val DATA_AUX_POWER = 32
    const val DATA_FISH_X = 33
    const val DATA_FISH_Z = 34
    const val DATA_FISH_ANGLE = 35
    const val DATA_POTION_AUX_VALUE = 36 //short

    const val DATA_LEAD_HOLDER_EID = 37 //long

    const val DATA_SCALE = 38 //float

    const val DATA_HAS_NPC_COMPONENT = 39 //byte

    const val DATA_NPC_SKIN_ID = 40 //string

    const val DATA_URL_TAG = 41 //string

    const val DATA_MAX_AIR = 42 //short

    const val DATA_MARK_VARIANT = 43 //int

    const val DATA_CONTAINER_TYPE = 44 //byte

    const val DATA_CONTAINER_BASE_SIZE = 45 //int

    const val DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH = 46 //int

    const val DATA_BLOCK_TARGET = 47 //block coords (ender crystal)

    const val DATA_WITHER_INVULNERABLE_TICKS = 48 //int

    const val DATA_WITHER_TARGET_1 = 49 //long

    const val DATA_WITHER_TARGET_2 = 50 //long

    const val DATA_WITHER_TARGET_3 = 51 //long

    const val DATA_AERIAL_ATTACK = 52
    const val DATA_BOUNDING_BOX_WIDTH = 53 //float

    const val DATA_BOUNDING_BOX_HEIGHT = 54 //float

    const val DATA_FUSE_LENGTH = 55 //int

    const val DATA_RIDER_SEAT_POSITION = 56 //vector3f

    const val DATA_RIDER_ROTATION_LOCKED = 57 //byte

    const val DATA_RIDER_MAX_ROTATION = 58 //float

    const val DATA_RIDER_MIN_ROTATION = 59 //float

    const val DATA_RIDER_ROTATION_OFFSET = 60
    const val DATA_AREA_EFFECT_CLOUD_RADIUS = 61 //float

    const val DATA_AREA_EFFECT_CLOUD_WAITING = 62 //int

    const val DATA_AREA_EFFECT_CLOUD_PARTICLE_ID = 63 //int

    const val DATA_SHULKER_PEEK_ID = 64 //int

    const val DATA_SHULKER_ATTACH_FACE = 65 //byte

    const val DATA_SHULKER_ATTACHED = 66 //short

    const val DATA_SHULKER_ATTACH_POS = 67 //block coords

    const val DATA_TRADING_PLAYER_EID = 68 //long

    const val DATA_TRADING_CAREER = 69
    const val DATA_HAS_COMMAND_BLOCK = 70
    const val DATA_COMMAND_BLOCK_COMMAND = 71 //string

    const val DATA_COMMAND_BLOCK_LAST_OUTPUT = 72 //string

    const val DATA_COMMAND_BLOCK_TRACK_OUTPUT = 73 //byte

    const val DATA_CONTROLLING_RIDER_SEAT_NUMBER = 74 //byte

    const val DATA_STRENGTH = 75 //int

    const val DATA_MAX_STRENGTH = 76 //int

    const val DATA_SPELL_CASTING_COLOR = 77 //int

    const val DATA_LIMITED_LIFE = 78
    const val DATA_ARMOR_STAND_POSE_INDEX = 79 // int

    const val DATA_ENDER_CRYSTAL_TIME_OFFSET = 80 // int

    const val DATA_ALWAYS_SHOW_NAMETAG = 81 // byte

    const val DATA_COLOR_2 = 82 // byte

    const val DATA_NAME_AUTHOR = 83
    const val DATA_SCORE_TAG = 84 // String

    const val DATA_BALLOON_ATTACHED_ENTITY = 85 // long

    const val DATA_PUFFERFISH_SIZE = 86
    const val DATA_BUBBLE_TIME = 87
    const val DATA_AGENT = 88
    const val DATA_SITTING_AMOUNT = 89
    const val DATA_SITTING_AMOUNT_PREVIOUS = 90
    const val DATA_EATING_COUNTER = 91
    const val DATA_FLAGS_EXTENDED = 92
    const val DATA_LAYING_AMOUNT = 93
    const val DATA_LAYING_AMOUNT_PREVIOUS = 94
    const val DATA_DURATION = 95
    const val DATA_SPAWN_TIME = 96
    const val DATA_CHANGE_RATE = 97
    const val DATA_CHANGE_ON_PICKUP = 98
    const val DATA_PICKUP_COUNT = 99
    const val DATA_INTERACTIVE_TAG = 100 //string (button text)

    const val DATA_TRADE_TIER = 101
    const val DATA_MAX_TRADE_TIER = 102
    const val DATA_TRADE_EXPERIENCE = 103
    const val DATA_SKIN_ID = 104 // int ???

    const val DATA_SPAWNING_FRAMES = 105
    const val DATA_COMMAND_BLOCK_TICK_DELAY = 106
    const val DATA_COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK = 107
    const val DATA_AMBIENT_SOUND_INTERVAL = 108
    const val DATA_AMBIENT_SOUND_INTERVAL_RANGE = 109
    const val DATA_AMBIENT_SOUND_EVENT_NAME = 110
    const val DATA_FALL_DAMAGE_MULTIPLIER = 111
    const val DATA_NAME_RAW_TEXT = 112
    const val DATA_CAN_RIDE_TARGET = 113
    const val DATA_LOW_TIER_CURED_DISCOUNT = 114
    const val DATA_HIGH_TIER_CURED_DISCOUNT = 115
    const val DATA_NEARBY_CURED_DISCOUNT = 116
    const val DATA_NEARBY_CURED_DISCOUNT_TIMESTAMP = 117
    const val DATA_HITBOX = 118
    const val DATA_IS_BUOYANT = 119
    const val DATA_BASE_RUNTIME_ID = 120
    const val DATA_FREEZING_EFFECT_STRENGTH = 121
    const val DATA_BUOYANCY_DATA = 122
    const val DATA_GOAT_HORN_COUNT = 123
    const val DATA_UPDATE_PROPERTIES = 124


    const val DATA_FLAG_CONVERTING = 12
    const val DATA_FLAG_CRITICAL = 13
    const val DATA_FLAG_CAN_SHOW_NAMETAG = 14
    const val DATA_FLAG_ALWAYS_SHOW_NAMETAG = 15
    const val DATA_FLAG_IMMOBILE = 16
    const val DATA_FLAG_NO_AI = 16
    const val DATA_FLAG_SILENT = 17
    const val DATA_FLAG_WALLCLIMBING = 18
    const val DATA_FLAG_CAN_CLIMB = 19
    const val DATA_FLAG_SWIMMER = 20
    const val DATA_FLAG_CAN_FLY = 21
    const val DATA_FLAG_WALKER = 22
    const val DATA_FLAG_RESTING = 23
    const val DATA_FLAG_SITTING = 24
    const val DATA_FLAG_ANGRY = 25
    const val DATA_FLAG_INTERESTED = 26
    const val DATA_FLAG_CHARGED = 27
    const val DATA_FLAG_TAMED = 28
    const val DATA_FLAG_ORPHANED = 29
    const val DATA_FLAG_LEASHED = 30
    const val DATA_FLAG_SHEARED = 31
    const val DATA_FLAG_GLIDING = 32
    const val DATA_FLAG_ELDER = 33
    const val DATA_FLAG_MOVING = 34
    const val DATA_FLAG_BREATHING = 35
    const val DATA_FLAG_CHESTED = 36
    const val DATA_FLAG_STACKABLE = 37
    const val DATA_FLAG_SHOWBASE = 38
    const val DATA_FLAG_REARING = 39
    const val DATA_FLAG_VIBRATING = 40
    const val DATA_FLAG_IDLING = 41
    const val DATA_FLAG_EVOKER_SPELL = 42
    const val DATA_FLAG_CHARGE_ATTACK = 43
    const val DATA_FLAG_WASD_CONTROLLED = 44
    const val DATA_FLAG_CAN_POWER_JUMP = 45
    const val DATA_FLAG_LINGER = 46
    const val DATA_FLAG_HAS_COLLISION = 47
    const val DATA_FLAG_GRAVITY = 48
    const val DATA_FLAG_FIRE_IMMUNE = 49
    const val DATA_FLAG_DANCING = 50
    const val DATA_FLAG_ENCHANTED = 51
    const val DATA_FLAG_SHOW_TRIDENT_ROPE =
        52 // tridents show an animated rope when enchanted with loyalty after they are thrown and return to their owner. To be combined with DATA_OWNER_EID

    const val DATA_FLAG_CONTAINER_PRIVATE = 53 //inventory is private, doesn't drop contents when killed if true

    const val DATA_FLAG_IS_TRANSFORMING = 54
    const val DATA_FLAG_SPIN_ATTACK = 55
    const val DATA_FLAG_SWIMMING = 56
    const val DATA_FLAG_BRIBED = 57 //dolphins have this set when they go to find treasure for the player

    const val DATA_FLAG_PREGNANT = 58
    const val DATA_FLAG_LAYING_EGG = 59
    const val DATA_FLAG_RIDER_CAN_PICK = 60
    const val DATA_FLAG_TRANSITION_SETTING = 61
    const val DATA_FLAG_EATING = 62
    const val DATA_FLAG_LAYING_DOWN = 63
    const val DATA_FLAG_SNEEZING = 64
    const val DATA_FLAG_TRUSTING = 65
    const val DATA_FLAG_ROLLING = 66
    const val DATA_FLAG_SCARED = 67
    const val DATA_FLAG_IN_SCAFFOLDING = 68
    const val DATA_FLAG_OVER_SCAFFOLDING = 69
    const val DATA_FLAG_FALL_THROUGH_SCAFFOLDING = 70
    const val DATA_FLAG_BLOCKING = 71
    const val DATA_FLAG_TRANSITION_BLOCKING = 72
    const val DATA_FLAG_BLOCKED_USING_SHIELD = 73
    const val DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD = 74
    const val DATA_FLAG_SLEEPING = 75
    const val DATA_FLAG_ENTITY_GROW_UP = 76
    const val DATA_FLAG_TRADE_INTEREST = 77
    const val DATA_FLAG_DOOR_BREAKER = 78
    const val DATA_FLAG_BREAKING_OBSTRUCTION = 79
    const val DATA_FLAG_DOOR_OPENER = 80
    const val DATA_FLAG_IS_ILLAGER_CAPTAIN = 81
    const val DATA_FLAG_STUNNED = 82
    const val DATA_FLAG_ROARING = 83
    const val DATA_FLAG_DELAYED_ATTACK = 84
    const val DATA_FLAG_IS_AVOIDING_MOBS = 85
    const val DATA_FLAG_IS_AVOIDING_BLOCKS = 86
    const val DATA_FLAG_FACING_TARGET_TO_RANGE_ATTACK = 87
    const val DATA_FLAG_HIDDEN_WHEN_INVISIBLE = 88
    const val DATA_FLAG_IS_IN_UI = 89
    const val DATA_FLAG_STALKING = 90
    const val DATA_FLAG_EMOTING = 91
    const val DATA_FLAG_CELEBRATING = 92
    const val DATA_FLAG_ADMIRING = 93
    const val DATA_FLAG_CELEBRATING_SPECIAL = 94
    const val DATA_FLAG_RAM_ATTACK = 96
    const val DATA_FLAG_PLAYING_DEAD = 97
    const val DATA_FLAG_IN_ASCENDABLE_BLOCK = 98
    const val DATA_FLAG_OVER_DESCENDABLE_BLOCK = 99

}