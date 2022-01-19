package bedrockDragon.network.raknet.protocol.game.type.gamerule


//see Nukkit GameRules
enum class GameRule(val s: String) {

    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"),
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_INSOMNIA("doInsomnia"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn"),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    FREEZE_DAMAGE("freezeDamage"),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit"),
    KEEP_INVENTORY("keepInventory"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_COORDINATES("showCoordinates"),
    SHOW_DEATH_MESSAGES("showDeathMessages"),
    SPAWN_RADIUS("spawnRadius"),
    TNT_EXPLODES("tntExplodes"),
    SHOW_TAGS("showTags");

    companion object {
        fun stringToEnum(s: String): GameRule? {
            for(rule in values()) {
                if(rule.name.equals(s, true)) {
                    return rule
                }
            }
            return null
        }
    }
}