package bedrockDragon.network.raknet.protocol.game.type.gamerule

import java.util.*

object GameRules {
    val gameRules = EnumMap<GameRule, Rule<*>>(GameRule::class.java)
    var dirty = false


    init {
        gameRules[GameRule.COMMAND_BLOCKS_ENABLED] = Rule(true)
        gameRules[GameRule.COMMAND_BLOCK_OUTPUT] = Rule(true)
        gameRules[GameRule.DO_DAYLIGHT_CYCLE] = Rule(true)
        gameRules[GameRule.DO_ENTITY_DROPS] = Rule(true)
        gameRules[GameRule.DO_FIRE_TICK] = Rule(true)
        gameRules[GameRule.DO_INSOMNIA] = Rule(true)
        gameRules[GameRule.DO_IMMEDIATE_RESPAWN] = Rule(true)
        gameRules[GameRule.DO_MOB_LOOT] = Rule(true)
        gameRules[GameRule.DO_MOB_SPAWNING] = Rule(true)
        gameRules[GameRule.DO_TILE_DROPS] = Rule(true)
        gameRules[GameRule.DO_WEATHER_CYCLE] = Rule(true)
        gameRules[GameRule.DROWNING_DAMAGE] = Rule(true)
        gameRules[GameRule.FALL_DAMAGE] = Rule(true)
        gameRules[GameRule.FIRE_DAMAGE] = Rule(true)
        gameRules[GameRule.FREEZE_DAMAGE] = Rule(true)
        gameRules[GameRule.FUNCTION_COMMAND_LIMIT] = Rule(10000)
        gameRules[GameRule.KEEP_INVENTORY] = Rule(false)
        gameRules[GameRule.MAX_COMMAND_CHAIN_LENGTH] = Rule(65536)
        gameRules[GameRule.MOB_GRIEFING] = Rule(true)
        gameRules[GameRule.NATURAL_REGENERATION] = Rule(true)
        gameRules[GameRule.PVP] = Rule(true)
        gameRules[GameRule.RANDOM_TICK_SPEED] = Rule(3)
        gameRules[GameRule.SEND_COMMAND_FEEDBACK] = Rule(true)
        gameRules[GameRule.SHOW_COORDINATES] = Rule(true)
        gameRules[GameRule.SHOW_DEATH_MESSAGES] = Rule(true)
        gameRules[GameRule.SPAWN_RADIUS] = Rule(true)
        gameRules[GameRule.TNT_EXPLODES] = Rule(true)
        gameRules[GameRule.SHOW_TAGS] = Rule(true)
    }



}