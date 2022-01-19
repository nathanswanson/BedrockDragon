package bedrockDragon.network.raknet.protocol.game.type.gamerule

import java.util.*

object GameRules {
    val gameRules = EnumMap<GameRule, Rule<*>>(GameRule::class.java)
    var dirty = false


    init {
        gameRules[GameRule.COMMAND_BLOCKS_ENABLED] = Rule(true)
        gameRules.put(GameRule.COMMAND_BLOCK_OUTPUT, Rule(true))
        gameRules.put(GameRule.DO_DAYLIGHT_CYCLE, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_ENTITY_DROPS, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_FIRE_TICK, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_INSOMNIA, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_IMMEDIATE_RESPAWN, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_MOB_LOOT, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_MOB_SPAWNING, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_TILE_DROPS, Rule<Boolean>(true))
        gameRules.put(GameRule.DO_WEATHER_CYCLE, Rule<Boolean>(true))
        gameRules.put(GameRule.DROWNING_DAMAGE, Rule<Boolean>(true))
        gameRules.put(GameRule.FALL_DAMAGE, Rule<Boolean>(true))
        gameRules.put(GameRule.FIRE_DAMAGE, Rule<Boolean>(true))
        gameRules.put(GameRule.FREEZE_DAMAGE, Rule<Boolean>(true))
        gameRules.put(GameRule.FUNCTION_COMMAND_LIMIT, Rule<Int>(10000))
        gameRules.put(GameRule.KEEP_INVENTORY, Rule<Boolean>(true))
        gameRules.put(GameRule.MAX_COMMAND_CHAIN_LENGTH, Rule<Int>(65536))
        gameRules.put(GameRule.MOB_GRIEFING, Rule<Boolean>(true))
        gameRules.put(GameRule.NATURAL_REGENERATION, Rule<Boolean>(true))
        gameRules.put(GameRule.PVP, Rule<Boolean>(true))
        gameRules.put(GameRule.RANDOM_TICK_SPEED, Rule<Int>(3))
        gameRules.put(GameRule.SEND_COMMAND_FEEDBACK, Rule<Boolean>(true))
        gameRules.put(GameRule.SHOW_COORDINATES, Rule<Boolean>(true))
        gameRules.put(GameRule.SHOW_DEATH_MESSAGES, Rule<Boolean>(true))
        gameRules.put(GameRule.SPAWN_RADIUS, Rule<Boolean>(true))
        gameRules.put(GameRule.TNT_EXPLODES, Rule<Boolean>(true))
        gameRules.put(GameRule.SHOW_TAGS, Rule<Boolean>(true))
    }



}