package bedrockDragon.network.raknet.protocol.game.type

object AttributeBR {
    const val ABSORPTION = 0
    const val SATURATION = 1
    const val EXHAUSTION = 2
    const val KNOCKBACK_RESISTANCE = 3
    const val HEALTH = 4
    const val MOVEMENT = 5
    const val FOLLOW_RANGE = 6
    const val PLAYER_HUNGER = 7
    const val ATTACK_DAMAGE = 8
    const val PLAYER_LEVEL = 9
    const val PLAYER_EXPERIENCE = 10
    const val LUCK = 11

        val attributes = arrayOf(
            Attribute(ABSORPTION, "minecraft:absorption", 0.0f, Float.MAX_VALUE, 0.0f), //absorption
            Attribute(SATURATION, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f), //saturation
            Attribute(EXHAUSTION, "minecraft:player.exhaustion", 0.0f, 5.00f, 0.0f),
            Attribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0.0f, 1.00f, 0.41f),
            Attribute(HEALTH, "minecraft:health", 0.00f, 20.00f, 20.00f),
            Attribute(MOVEMENT, "minecraft:movement", 0.00f, Float.MAX_VALUE, 0.2f),
            //Attribute(6, "minecraft:follow_range", 0.00f, 2048f, 16f),
            Attribute(PLAYER_HUNGER, "minecraft:player.hunger", 0.00f, 20.00f, 20.00f),
            //Attribute(8, "minecraft:attack_damage", 0.00f, Float.MAX_VALUE, 1f),
            Attribute(PLAYER_LEVEL, "minecraft:player.level", 0.00f, 24791f, 0.00f),
            Attribute(PLAYER_EXPERIENCE, "minecraft:player.experience", 0.00f, 1f, 0f),
            Attribute(LUCK, "minecraft:luck", -1024f, 1024f, 0f)
        )

    operator fun get(value: Int): Attribute {
        return attributes.filter { it.id == value }[0]
    }

    data class Attribute(val id: Int,val name: String,val minValue: Float,val maxValue: Float,val defaultValue: Float)
    {
        var value: Float? = null

        fun getValueOrDefault(): Float {
            return value ?: defaultValue
        }
    }
}